package com.testmax.runner;

public class MyRunner {
    public static void main(String[] args) {
        // TestSuite args – přesné pozice, jak je čte TestSuite/TestEngine:
        // 0=name, 1=page, 2=browsers, 3=env, 4=datasetExt, 5=overrideAttrs,
        // 6=groupByThread, 7=baseUrl, 8=action, 9=threads, 10=timeout,
        // 11=buildno, 12=buildid (v engine se používá jako dynamicPath),
        // 13=jenkinsurl, 14=jobname, 15=workspace, 16=nodename
        String[] a = new String[17];
        a[0]  = "HelloSuite";
        a[1]  = "HelloDLMS"; // = data/module/JavaUnit/TestNgTest.xml
        a[2]  = "";                    // browsers
        a[3]  = "";                    // env
        a[4]  = "";                    // dataset extension
        a[5]  = "";                    // override attributes
        a[6]  = "";                    // group by thread
        a[7]  = "";                    // baseUrl (prázdné je OK)
        a[8]  = "MockFrame";                 // = <action name="TC1"> (bez Selenium)
        a[9]  = "1";                   // threads
        a[10] = "30";                  // timeout v sekundách
        a[11] = "";                    // buildno
        a[12] = Long.toString(System.currentTimeMillis()); // buildid/dynamicPath
        a[13] = "";                    // jenkinsurl
        a[14] = "";                    // jobname
        a[15] = System.getProperty("user.dir"); // workspace = aktuální adresář projektu
        a[16] = "local";               // nodename

        TestEngine engine = new TestEngine();
        engine.start(a);
    }
}