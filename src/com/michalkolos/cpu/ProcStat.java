/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos.cpu;

import com.michalkolos.cpu.data.CpuCoreTimes;
import com.michalkolos.cpu.data.CpuCoreUsageDetails;
import com.michalkolos.input.LocalFile;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Provides data gathered from "/proc/stat" file. This includes current CPU
 * usage per core, total number of context switches, time of system boot,
 * processes created since boot, processes that are currently running and
 * processes that are blocked by I/O request.
 */
public class ProcStat {

	/**
	 * Absolute path to the "proc/stat" file.
	 */
	public static final String SYS_FILE_PATH = "/proc/stat";

	private final LocalFile statFile;

	/**
	 * Number of logical CPU cores. Calculated by counting rows in stat file
	 * that start with "cpu##" string.
	 */
	private int cpuCoresCount = 0;

	/**
	 * Data for all cores combined measured in previous execution of
	 * dataAcquisitionLoop() method.
	 */
	private CpuCoreTimes previousTotalTimes;

	/**
	 * Data for individual logical cores measured in previous execution of
	 * dataAcquisitionLoop() method.
	 */
	private final List<CpuCoreTimes> previousCoreTimes;

	/**
	 * Current CPU usage data.
	 */
	private CpuCoreUsageDetails totalCpuUsage;

	/**
	 * Curent usage data per logical core. List index represents core ID number.
	 */
	private final List<CpuCoreUsageDetails> coreCpuUsage;

	private long contextSwitchesCount = 0;
	private Instant bootTime = Instant.MIN;
	private long processesCreated = 0;
	private long processesRunning = 0;
	private long processesBlockedOnIo = 0;



	public ProcStat() throws IOException {
		this.statFile = new LocalFile(SYS_FILE_PATH);
		List<String> statFileLines = statFile.readLines();

		this.cpuCoresCount = countCores(statFileLines);

		this.previousTotalTimes = new CpuCoreTimes();
		this.previousCoreTimes = Stream.generate(CpuCoreTimes::new)
				.limit(this.cpuCoresCount)
				.collect(Collectors.toList());

		this.totalCpuUsage = new CpuCoreUsageDetails();

		this.coreCpuUsage = Stream.generate(CpuCoreUsageDetails::new)
				.limit(this.cpuCoresCount)
				.collect(Collectors.toList());
	}


	//  TODO: Research core count changing in time on some systems (eg. Android
	//   phones). https://stackoverflow.com/questions/22405403/android-cpu-cores-reported-in-proc-stat


	/**
	 * Calculates number of logical cores by counting rows in stat file that
	 * start with "cpu##" string.
	 * @return Number of logical CPU cores.
	 */
	private int countCores(List<String> lines) {
		return (int)Optional.ofNullable(lines).orElse(new ArrayList<>())
				.stream()
				.filter((String line) -> line.matches("cpu[0-9]+.*"))
				.count();
	}


	/**
	 * Reads CPU data from appropriate line in the "proc/stat" file.
	 * @param line One line from the "proc/stat" file begging with "cpu".
	 * @return Parsed data from every column in the line.
	 */
	private Optional<CpuCoreTimes> parseCpuLine(String line) {
		Optional<CpuCoreTimes> parsedDataOptional = Optional.empty();

		List<String> fields = Arrays.asList(line.split(" +"));
		if(fields.size() >= 10) {
			try{
				CpuCoreTimes parsedData = new CpuCoreTimes();

				parsedData.setUser(Long.parseLong(fields.get(1)));
				parsedData.setNice(Long.parseLong(fields.get(2)));
				parsedData.setSystem(Long.parseLong(fields.get(3)));
				parsedData.setIdle(Long.parseLong(fields.get(4)));
				parsedData.setIowait(Long.parseLong(fields.get(5)));
				parsedData.setIrq(Long.parseLong(fields.get(6)));
				parsedData.setSoftirq(Long.parseLong(fields.get(7)));
				parsedData.setSteal(Long.parseLong(fields.get(8)));
				parsedData.setGuest(Long.parseLong(fields.get(9)));

				parsedDataOptional = Optional.of(parsedData);
			} catch (NumberFormatException e) {
				e.printStackTrace();
//				TODO: Logging
			}
		}

		return parsedDataOptional;
	}


	/**
	 * Reads value from a single line of "proc/stat" file that golds single name -
	 * value pair.
	 * @param line One line from the "proc/stat" file that holds single name -
	 *             numeric value pair.
	 * @return  Parsed value.
	 */
	private Optional<Long> parseSingleValueLine(String line) {
		List<String> fields = Arrays.asList(line.split(" "));
		Long parsedVal = null;

		try {
			parsedVal = Long.parseLong(fields.get(1));
		} catch (NumberFormatException e) {
//			TODO: Logg parsing error.
		}

		return Optional.ofNullable(parsedVal);
	}


	/**
	 * Calculates different usage modes ratios based on raw elapsed time data.
	 * @param previous Data gathered in the previous execution of the
	 *                 dataAcquisitionLoop() method.
	 * @param current Data gathered in the most recent execution of the
	 *                dataAcquisitionLoop() method.
	 * @return Object containing all the usage data.
	 */
	private CpuCoreUsageDetails calculateCpuUsage(CpuCoreTimes previous, CpuCoreTimes current) {

		long previousIdle = previous.getIdle() + previous.getIowait();
		long currentIdle = current.getIdle() + current.getIowait();



		long previousNonIdle = previous.getUser() + previous.getNice()
				+ previous.getSystem() + previous.getIrq()
				+ previous.getSoftirq() + previous.getSteal();

		long currentNonIdle =  current.getUser() + current.getNice()
				+ current.getSystem() + current.getIrq()
				+ current.getSoftirq() + current.getSteal();

		long previousTotal = previousIdle + previousNonIdle;
		long currentTotal = currentIdle + currentNonIdle;

		long totalDifference = currentTotal - previousTotal;
		long idleDifference = currentIdle - previousIdle;

		CpuCoreUsageDetails usageDetails = new CpuCoreUsageDetails();
		usageDetails.setTotalUsage((float)(totalDifference - idleDifference)
				/ totalDifference);
		usageDetails.setIoUsage((float)(current.getIowait() - previous.getIowait())
				/ totalDifference);
		usageDetails.setSystemUsage((float)(current.getSystem() - previous.getSystem())
				/ totalDifference);
		usageDetails.setUserUsage((float)(current.getUser() - previous.getUser())
				/ totalDifference);
		usageDetails.setHardIrqUsage((float)(current.getIrq() - previous.getIrq())
				/ totalDifference);
		usageDetails.setSoftIrqUsage((float)(current.getSoftirq() - previous.getSoftirq())
				/ totalDifference);


		return usageDetails;
	}


	/**
	 * Method that needs to be called periodically to gather data from the
	 * "proc/stat file. This system file is constantly updated with cpu usage
	 * statistics for entire CPU as well as for individual logical cores.
	 * @throws IOException Exception thrown when the accessed file is
	 * unavailable.
	 */
	public void dataAcquisition() throws IOException{

		List<String> statLines = statFile.readLines();
		//  Check if stat file contains enough lines:
		if(statLines.size() < this.cpuCoresCount + 7) { return; }

		ListIterator<String> linesIterator = statLines.listIterator();

		//  Total CPU usage.
		parseCpuLine(linesIterator.next()).ifPresent(current -> {
			this. totalCpuUsage = calculateCpuUsage(this.previousTotalTimes, current);
		});

		//  CPU usage per core.
		for(int i = 0; i < cpuCoresCount; i++) {
			Optional<CpuCoreTimes> currentCpuCoreTimes = parseCpuLine(linesIterator.next());
			if(currentCpuCoreTimes.isPresent()) {
				this.coreCpuUsage.set(i, calculateCpuUsage(
						this.previousCoreTimes.get(i), currentCpuCoreTimes.get()));

				this.previousCoreTimes.set(i, currentCpuCoreTimes.get());			}
		}


		//  System interrupts section - not recorded.
		linesIterator.next();

		//  Total number of context switches since boot.
		parseSingleValueLine(linesIterator.next())
				.ifPresent((val)->{ this.contextSwitchesCount = val; });
//		this.contextSwitchesCount = parseSingleValueLine(reader.readLine());

		//  Time of boot.
		parseSingleValueLine(linesIterator.next())
				.ifPresent((val)->{ this.bootTime = Instant.ofEpochSecond(val); });
//		long bootTimeEpoch = parseSingleValueLine(reader.readLine());
//		this.bootTime = Instant.ofEpochSecond(bootTimeEpoch);

		//  Total number of processes created since boot.
		parseSingleValueLine(linesIterator.next())
				.ifPresent((val)->{ this.processesCreated = val; });
//		this.processesCreated = parseSingleValueLine(reader.readLine());

		//  Number of currently running processes.
		parseSingleValueLine(linesIterator.next())
				.ifPresent((val)->{ this.processesRunning = val; });
//		this.processesRunning = parseSingleValueLine(reader.readLine());

		//  Number of processes currently being blocked on IO requests.
		parseSingleValueLine(linesIterator.next())
				.ifPresent((val)->{ this.processesBlockedOnIo = val; });
//		this.processesBlockedOnIo = parseSingleValueLine(reader.readLine());
	}


	public int getCpuCoresCount() {
		return cpuCoresCount;
	}

	public float getTotalCpuUsage() {
		return totalCpuUsage.getTotalUsage();
	}

	public CpuCoreUsageDetails getCoreCpuUsageDetails(int coreNumber) {
		if(coreNumber >= 0 && coreNumber < coreCpuUsage.size()) {
			return coreCpuUsage.get(coreNumber);
		} else {
			return new CpuCoreUsageDetails();
		}
	}

	public float getCoreCpuUsage(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getTotalUsage();
	}

	public float getCoreCpuIoWait(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getIoUsage();
	}

	public float getCoreCpuUserSpaceUsage(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getUserUsage();
	}

	public float getCoreCpuSystemSpaceUsage(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getSystemUsage();
	}

	public float getCoreCpuSoftwareIrqUsage(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getSoftIrqUsage();
	}
	public float getCoreCpuHardwareIrqUsage(int coreNumber) {
		return getCoreCpuUsageDetails(coreNumber).getHardIrqUsage();
	}


	public long getContextSwitchesCount() {
		return contextSwitchesCount;
	}

	public Instant getBootTime() {
		return bootTime;
	}

	public long getProcessesCreated() {
		return processesCreated;
	}

	public long getProcessesRunning() {
		return processesRunning;
	}

	public long getProcessesBlockedOnIo() {
		return processesBlockedOnIo;
	}




	private String floatToPercent(float fraction) {
		return String.format("%05.2f", fraction * 100);
	}
	private String intLeadingZeros(int number) {
		return String.format("%02d", number);
	}

	public String coreStatsToString(CpuCoreUsageDetails usage) {
		return floatToPercent(usage.getTotalUsage()) +
				" (I/O: " +
				floatToPercent(usage.getIoUsage()) +
				", user: " +
				floatToPercent(usage.getUserUsage()) +
				", system: " +
				floatToPercent(usage.getSystemUsage()) +
				", soft IRQ: " +
				floatToPercent(usage.getSoftIrqUsage()) +
				", hard IRQ: " +
				floatToPercent(usage.getHardIrqUsage()) +
				")" +
				System.lineSeparator();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Total: ")
				.append(coreStatsToString(totalCpuUsage));

		for(int i = 0; i < coreCpuUsage.size(); i++) {
			sb.append("CPU")
					.append(intLeadingZeros(i))
					.append(": ")
					.append(coreStatsToString(coreCpuUsage.get(i)));
		}

		sb.append("Boot time:         ")
				.append(getBootTime().toString())
				.append(System.lineSeparator());

		sb.append("Context switches:  ")
				.append(getContextSwitchesCount())
				.append(System.lineSeparator());

		sb.append("Processes created: ")
				.append(getProcessesCreated())
				.append(System.lineSeparator());

		sb.append("Processes running: ")
				.append(getProcessesRunning())
				.append(System.lineSeparator());

		sb.append("Processes blocked: ")
				.append(getProcessesBlockedOnIo())
				.append(System.lineSeparator());

		return sb.toString();
	}
}
