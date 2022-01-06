/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos;

import com.michalkolos.cpu.CpuFrequency;
import com.michalkolos.cpu.ProcStat;
import com.michalkolos.hwmon.HwmonExplorrer;
import com.sun.management.OperatingSystemMXBean;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello World!");

        OperatingSystemMXBean systemBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();


        ProcStat procStat = null;
        try {
            procStat = new ProcStat();
        } catch (IOException e) {
            e.printStackTrace();
        }


        CpuFrequency cpuFrequency = new CpuFrequency();
        HwmonExplorrer hwmonExplorrer = new HwmonExplorrer();

        for(int i = 0; i < 100; i++) {

            try {
                procStat.dataAcquisitionLoop();
                cpuFrequency.dataAcquisitionLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }

            procStatPrint(systemBean, procStat);
            cpuFreqPrint(cpuFrequency);
//            System.out.println(hwmonExplorrer.allSubsystemsToString());


            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void cpuFreqPrint(CpuFrequency cpuFrequency) {
        StringBuilder sb = new StringBuilder();

        for(int j = 0; j < cpuFrequency.getLogicalCores(); j++) {
            sb.append("CPU").append(j)
                    .append(": ")
                    .append(cpuFrequency.getCoreFrequency(j))
                    .append("MHz")
                    .append(System.lineSeparator());
        }

        sb.append(System.lineSeparator())
                .append("=============================================")
                .append(System.lineSeparator());

        System.out.println(sb.toString());
    }


    private static void procStatPrint(OperatingSystemMXBean systemBean, ProcStat procStat) {
        StringBuilder sb = new StringBuilder();

        sb.append("Total CPU: ")
                .append(procStat.getTotalCpuUsage())
                .append("  -  ")
                .append(String.valueOf(systemBean.getSystemCpuLoad()))
                .append(System.lineSeparator());

        for(int j = 0; j < procStat.getCpuCoresCount(); j++) {
            sb.append("CPU")
                    .append(j)
                    .append(": ")
                    .append(procStat.getCoreCpuUsage(j))
                    .append(" (I/O: ")
                    .append(procStat.getCoreCpuIoWait(j))
                    .append(" , User: ")
                    .append(procStat.getCoreCpuUserSpaceUsage(j))
                    .append(" , System: ")
                    .append(procStat.getCoreCpuSystemSpaceUsage(j))
                    .append(" , Soft IRQ: ")
                    .append(procStat.getCoreCpuSoftwareIrqUsage(j))
                    .append(" , Hard IRQ: ")
                    .append(procStat.getCoreCpuHardwareIrqUsage(j))
                    .append(" ,     Total: ")
                    .append(procStat.getCoreCpuIoWait(j) +
                            procStat.getCoreCpuUserSpaceUsage(j) +
                            procStat.getCoreCpuSystemSpaceUsage(j) +
                            procStat.getCoreCpuSoftwareIrqUsage(j) +
                            procStat.getCoreCpuHardwareIrqUsage(j))
                    .append(") ")
                    .append(System.lineSeparator());
        }

        sb.append("Boot time: ")
                .append(procStat.getBootTime().toString())
                .append(System.lineSeparator());

        sb.append("Context switches: ")
                .append(procStat.getContextSwitchesCount())
                .append(System.lineSeparator());

        sb.append("Processes created: ")
                .append(procStat.getProcessesCreated())
                .append(System.lineSeparator());

        sb.append("Processes running: ")
                .append(procStat.getProcessesRunning())
                .append(System.lineSeparator());

        sb.append("Processes blocked: ")
                .append(procStat.getProcessesBlockedOnIo())
                .append(System.lineSeparator());

        sb.append(System.lineSeparator())
                .append("=============================================")
                .append(System.lineSeparator());

        System.out.println(sb.toString());
    }
}
