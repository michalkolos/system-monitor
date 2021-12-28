package com.michalkolos;

import com.michalkolos.cpu.ProcStat;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) {

        System.out.println("Hello World!");

        OperatingSystemMXBean systemBean =
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        ProcStat procStat = new ProcStat(16);

        for(int i = 0; i < 100; i++) {

            try {
                procStat.dataAcquisitionLoop();
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuilder sb = new StringBuilder();

            sb.append("Total CPU: ")
                    .append(procStat.getTotalCpuUsage())
                    .append("  -  ")
                    .append(String.valueOf(systemBean.getSystemCpuLoad()))
                    .append(System.lineSeparator());

            for(int j = 0; j < 16; j++) {
                sb.append("CPU")
                        .append(j)
                        .append(": ")
                        .append(procStat.getCoreCpuUsage(j))
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






            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
