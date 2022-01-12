/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos;

import com.michalkolos.cpu.CpuFrequency;
import com.michalkolos.cpu.CpuTemp;
import com.michalkolos.cpu.ProcStat;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello World!");

        OperatingSystemMXBean systemBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();



        try {
            ProcStat procStat = new ProcStat();
            CpuFrequency cpuFrequency = new CpuFrequency();
            CpuTemp cpuTemp = new CpuTemp();

            for(int i = 0; i < 100; i++) {
                System.out.println(cpuFrequency.toString());
                System.out.println(System.lineSeparator());
                System.out.println(cpuTemp.checkTemp().map(Objects::toString).orElse("NULL"));
                procStat.dataAcquisition();
                procStatPrint(systemBean, procStat);


                System.out.println(System.lineSeparator());
                System.out.println(System.lineSeparator());

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

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
