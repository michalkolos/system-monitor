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
                System.out.println(procStat.toString());


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
}
