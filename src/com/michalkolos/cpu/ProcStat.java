package com.michalkolos.cpu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcStat {

	private final int cpuCoresCount;

	private CpuTimes previousTotalTimes;
	private final List<CpuTimes> previousCoreTimes;

	private double totalCpuUsage;
	private final List<Double> coreCpuUsage;

	private long contextSwitchesCount = 0;
	private Instant bootTime = Instant.MIN;
	private long processesCreated = 0;
	private long processesRunning = 0;
	private long processesBlockedOnIo = 0;





	public ProcStat(int cpuCoresCount) {
		this.cpuCoresCount = cpuCoresCount;

		this.previousTotalTimes = new CpuTimes();
		this.previousCoreTimes = Stream.generate(CpuTimes::new)
				.limit(cpuCoresCount)
				.collect(Collectors.toList());

		this.totalCpuUsage = 0.0d;
		this.coreCpuUsage = new ArrayList<Double>(Collections.nCopies(cpuCoresCount, 0.0d));
	}





	private CpuTimes parseCpuLine(String line) {
		List<String> fields = Arrays.asList(line.split(" +"));

		CpuTimes parsedData = new CpuTimes();

		if(fields.size() >= 10) {
			parsedData.setUser(Long.parseLong(fields.get(1)));
			parsedData.setNice(Long.parseLong(fields.get(2)));
			parsedData.setSystem(Long.parseLong(fields.get(3)));
			parsedData.setIdle(Long.parseLong(fields.get(4)));
			parsedData.setIowait(Long.parseLong(fields.get(5)));
			parsedData.setIrq(Long.parseLong(fields.get(6)));
			parsedData.setSoftirq(Long.parseLong(fields.get(7)));
			parsedData.setSteal(Long.parseLong(fields.get(8)));
			parsedData.setGuest(Long.parseLong(fields.get(9)));
		}

		return parsedData;
	}

	private long parseSingleValueLine(String line) {
		List<String> fields = Arrays.asList(line.split(" "));

		return Long.parseLong(fields.get(1));
	}

	private double calculateCpuUsage(CpuTimes previous, CpuTimes current) {

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

		double cpuUsage = (double)(totalDifference - idleDifference) / totalDifference;

		if(cpuUsage < 0) {
			System.out.println();
		}

		return cpuUsage;
	}




	public void dataAcquisitionLoop() throws IOException{

		BufferedReader reader = new BufferedReader(new FileReader("/proc/stat"));

		//  Total CPU usage.
		CpuTimes currentCpuTimes = parseCpuLine(reader.readLine());
		this.totalCpuUsage = calculateCpuUsage(this.previousTotalTimes, currentCpuTimes);

		this.previousTotalTimes = currentCpuTimes;

		//  CPU usage per core.
		for(int i = 0; i < cpuCoresCount; i++) {
			currentCpuTimes = parseCpuLine(reader.readLine());
			this.coreCpuUsage.set(i, calculateCpuUsage(this.previousCoreTimes.get(i), currentCpuTimes));
			this.previousCoreTimes.set(i, currentCpuTimes);
		}

		//  System interrupts section - not recorded.
		reader.readLine();

		//  Total number of context switches since boot.
		this.contextSwitchesCount = parseSingleValueLine(reader.readLine());

		//  Time of boot.
		long bootTimeEpoch = parseSingleValueLine(reader.readLine());
		this.bootTime = Instant.ofEpochSecond(bootTimeEpoch);

		//  Total number of processes created since boot.
		this.processesCreated = parseSingleValueLine(reader.readLine());

		//  Number of currently running processes.
		this.processesRunning = parseSingleValueLine(reader.readLine());

		//  Number of processes currently being blocked on IO requests.
		this.processesBlockedOnIo = parseSingleValueLine(reader.readLine());

		reader.close();
	}


	public int getCpuCoresCount() {
		return cpuCoresCount;
	}

	public double getTotalCpuUsage() {
		return totalCpuUsage;
	}

	public double getCoreCpuUsage(int coreNumber) {
		if(coreNumber >= 0 && coreNumber < coreCpuUsage.size()) {
			return coreCpuUsage.get(coreNumber);
		} else {
			return 0.0f;
		}
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
}
