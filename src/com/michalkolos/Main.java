/*
 * Copyright (c) 2021 by Michal Kolosowski.
 */

package com.michalkolos;

import com.michalkolos.cpu.CpuFrequency;
import com.michalkolos.cpu.CpuTemp;
import com.michalkolos.cpu.ProcStat;
import com.sun.management.OperatingSystemMXBean;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");

        int count = Integer.parseInt(args[0].split("\n")[0]);
        String[] stringArray = args[0].split("\n")[1].split(" ");

        int[] array = new int[count];

        for(int i = 0; i < count; i++) {
            array[i] = Integer.parseInt(stringArray[i]);
        }

        int counter = 0;
        for(int i = 0; i < array.length - 3; i++) {
            if(array[i] == array[i + 1] + 1 && array[i + 1] == array[i + 2] + 1) {
                counter++;
            }
        }
        System.out.println(counter);

        for(String strVal : stringArray) {

        }


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
