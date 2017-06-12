package eta.runtime;

import java.util.List;
import java.util.ArrayList;

import static eta.runtime.Rts.stgExit;
import static eta.runtime.Rts.ExitCode.EXIT_FAILURE;
import static eta.runtime.Rts.ExitCode.EXIT_SUCCESS;
import static eta.runtime.RtsFlags.RtsOptsEnabled.*;
import static eta.runtime.RtsMessages.errorBelch;

public class RtsFlags {
    public static String progName;
    public static String[] fullProgArgs;
    public static List<String> progArgs = new ArrayList<String>();
    public static List<String> rtsArgs = new ArrayList<String>();

    public static class DebugFlags {
        public static boolean scheduler;
        public static boolean interpreter;
        public static boolean weak;
        public static boolean gccafs;
        public static boolean gc;
        public static boolean block;
        public static boolean sanity;
        public static boolean stable;
        public static boolean stm;
        public static boolean prof;
        public static boolean apply;
        public static boolean linker;
        public static boolean squeeze;
        public static boolean hpc;
        public static boolean sparks;
        public static boolean printStack;
    }

    public static class ParFlags {
        public static int maxWorkerCapabilities = 2 * getNumberOfProcessors() + 1;
        public static int maxLocalSparks = 4096;
        public static int minTSOIdleTime = 20;
    }

    public static void initDefaults() {
        RtsFlags.ParFlags.nNodes                 = 1;
        RtsFlags.ParFlags.maxLocalSparks         = 4096;
        RtsFlags.ParFlags.migrate                = true;
        RtsFlags.DebugFlags.scheduler            = false;
        RtsFlags.DebugFlags.interpreter          = false;
        RtsFlags.DebugFlags.weak                 = false;
        RtsFlags.DebugFlags.gccafs               = false;
        RtsFlags.DebugFlags.gc                   = false;
        RtsFlags.DebugFlags.sanity               = false;
        RtsFlags.DebugFlags.stable               = false;
        RtsFlags.DebugFlags.stm                  = false;
        RtsFlags.DebugFlags.prof                 = false;
        RtsFlags.DebugFlags.apply                = false;
        RtsFlags.DebugFlags.linker               = false;
        RtsFlags.DebugFlags.squeeze              = false;
        RtsFlags.DebugFlags.hpc                  = false;
        RtsFlags.DebugFlags.sparks               = false;
        RtsFlags.DebugFlags.printStack           = false;
    }

    public static void setup(String[] args,
                             RtsOptsEnabled rtsOptsEnabled,
                             String rtsOpts,
                             boolean isHsMain) {
        int argc = args.length;
        int totalArgs = argc;
        setProgName(args);
        progArgs.clear();
        int rtsArgc = 0;
        if (rtsOpts != null) {
            splitRtsFlags(rtsOpts);
            procRtsOpts(isHsMain, rtsArgc, RtsOptsAll);
            rtsArgc = rtsArgs.size();
        }
        String env = System.getenv("ETA_RTS");
        if (env != null) {
            splitRtsFlags(env);
            procRtsOpts(isHsMain, rtsArgc, rtsOptsEnabled);
            rtsArgc = rtsArgs.size();
        }

        int i = 0;
        boolean rts = false;
        for (rts = false; i < argc; i++) {
            String arg = args[i];
            if (arg.equals("--RTS")) {
                i++;
                break;
            } else if (arg.equals("--")) {
                break;
            } else if (arg.equals("+RTS")) {
                rts = true;
            } else if (arg.equals("-RTS")) {
                rts = false;
            } else if (rts) {
                appendRtsArg(arg);
            } else {
                progArgs.add(arg);
            }
        }

        for(; i < argc; i++) {
            String arg = args[i];
            progArgs.add(arg);
        }

        procRtsOpts(isHsMain, rtsArgc, rtsOptsEnabled);
        normaliseRtsOpts();
        /* TODO: set Program name */
    }

    public static void splitRtsFlags(String s) {
        String[] args = s.trim().split("\\s+");
        for (String arg: args) {
            appendRtsArg(arg);
        }
    }

    public static void procRtsOpts(boolean isHsMain,
                                   int rtsArgc) {
        if (rtsArgc >= rtsArgs.size()) return;
        boolean error = false;
        /* TODO: Check suid? */
        for (int i = rtsArgc; i < rtsArgs.size(); i++) {
            boolean optionChecked = false;
            String arg = rtsArgs.get(i);
            if (arg.charAt(0) != '-') {
                System.out.flush();
                errorBelch("unexpected RTS argument: %s", arg);
                error = true;
            } else {
                switch(arg.charAt(1)) {
                    case '?':
                        optionChecked = true;
                        error = true;
                        break;
                    case '-':
                        String option = arg.substring(2);
                        optionChecked = true;
                        if (option.equals("info")) {
                            printRtsInfo();
                            stgExit(EXIT_SUCCESS);
                        } else {
                            errorBelch("unknown RTS options: %s", arg);
                            error = true;
                        }
                        break;
                    case 'D':
                        optionChecked = true;
                        String rest = arg.substring(2);
                        for (char c: rest.toCharArray()) {
                            switch (c) {
                                case 's':
                                    RtsFlags.DebugFlags.scheduler = true;
                                    break;
                                case 'i':
                                    RtsFlags.DebugFlags.interpreter = true;
                                    break;
                                case 'w':
                                    RtsFlags.DebugFlags.weak = true;
                                    break;
                                case 'G':
                                    RtsFlags.DebugFlags.gccafs = true;
                                    break;
                                case 'g':
                                    RtsFlags.DebugFlags.gc = true;
                                    break;
                                case 'S':
                                    RtsFlags.DebugFlags.sanity = true;
                                    break;
                                case 't':
                                    RtsFlags.DebugFlags.stable = true;
                                    break;
                                case 'p':
                                    RtsFlags.DebugFlags.prof = true;
                                    break;
                                case 'l':
                                    RtsFlags.DebugFlags.linker = true;
                                    break;
                                case 'a':
                                    RtsFlags.DebugFlags.apply = true;
                                    break;
                                case 'm':
                                    RtsFlags.DebugFlags.stm = true;
                                    break;
                                case 'z':
                                    RtsFlags.DebugFlags.squeeze = true;
                                    break;
                                case 'c':
                                    RtsFlags.DebugFlags.hpc = true;
                                    break;
                                case 'r':
                                    RtsFlags.DebugFlags.sparks = true;
                                case 'k':
                                    RtsFlags.DebugFlags.printStack = true;
                                    break;
                                default:
                                    badOption(arg);
                            }
                        }
                        break;
                    case 'C':
                        optionChecked = true;
                        checkUnsafe(isHsMain, rtsOptsEnabled);
                        if (arg.length() == 2) {
                            RtsFlags.ConcFlags.ctxtSwitchTime = 0;
                        } else {
                            /* TODO: Catch exception */
                            RtsFlags.ConcFlags.ctxtSwitchTime =
                                (long)(1000 * Float.parseFloat(arg.substring(2)));
                        }
                        break;
                    case 'N':
                        optionChecked = true;
                        if (arg.length() == 2) {
                            RtsFlags.ParFlags.nNodes = 1;
                        } else {
                            int nNodes = getIntOrZero(arg.substring(2));
                            if (nNodes <= 0) {
                                errorBelch("bad value for -N");
                                error = true;
                            }
                            if (rtsOptsEnabled == RtsOptsSafeOnly &&
                                nNodes > getNumberOfProcessors()) {
                                errorRtsOptsDisabled(isHsMain, "Using large values for -N is not allowed by default. %s");
                                stgExit(EXIT_FAILURE);
                            }
                            RtsFlags.ParFlags.nNodes = nNodes;
                        }
                        break;
                    case 'e':
                        optionChecked = true;
                        checkUnsafe(isHsMain, rtsOptsEnabled);
                        if (arg.length() > 2) {
                            int maxLocalSparks = getIntOrZero(arg.substring(2));
                            if (maxLocalSparks <= 0) {
                                errorBelch("bad value for -e");
                                error = true;
                            }
                            RtsFlags.ParFlags.maxLocalSparks = maxLocalSparks;
                        }
                        break;
                    case 'q':
                        if (arg.charAt(2) == 'm') {
                            RtsFlags.ParFlags.migrate = false;
                        }
                        break;
                    default:
                        optionChecked = true;
                        errorBelch("unknown RTS option: %s", arg);
                        error = true;
                        break;
                }
                if (!optionChecked) {
                    errorBelch("Internal error in the RTS options parser");
                    stgExit(EXIT_FAILURE);
                }
            }
        }
        if (error) errorUsage();
    }

    public static void errorUsage() {
        System.out.flush();
        /* TODO: Usage text */
        System.out.println("Wrong usage. Consult the source code.");
        stgExit(EXIT_FAILURE);
    }

    public static void normaliseRtsOpts() {}

    public static int getIntOrZero(String s) {
        int res;
        try {
            res = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            res = 0;
        }
        return res;
    }

    public static int getNumberOfProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static void errorRtsOptsDisabled(boolean isHsMain, String s) {
        String advice;
        if (isHsMain) {
            advice = "Link with -rtsopts to enable them.";
        } else {
            advice = "Use Rts.initWithRtsOpts() to enable them.";
        }
        errorBelch(s, advice);
    }

    public static void badOption(String s) {
        errorBelch("bad RTS option: %s", s);
        stgExit(EXIT_FAILURE);
    }

    public static void appendRtsArg(String arg) {
        rtsArgs.add(arg);
    }

    public static void setProgName(String[] args) {
        /* TODO: Implement setting program name */
    }

    public enum RtsOptsEnabled {
        RtsOptsNone,
        RtsOptsSafeOnly,
        RtsOptsAll
    }

    public static void setFullProgArgs(String[] args) {
        fullProgArgs = args;
    }

    public static void printRtsInfo() {
        /* TODO: Implement */
    }
}
