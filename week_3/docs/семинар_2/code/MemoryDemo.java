import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

//java -Xms64m -Xmx128m -Xss256k MemoryDemo
//
//Где:
//-Xms64m   начальный размер Heap
//-Xmx128m  максимальный размер Heap
//-Xss256k  размер Stack для одного потока
//
//SLOW_MODE = true замедляет шаги демо паузами, чтобы в реальном времени
//успевать следить за графиками памяти (Live Charts) в IDEA; false — без пауз
public class MemoryDemo {



    private static final Runtime runtime = Runtime.getRuntime();

    // паузы между шагами демо для наблюдения за графиками в IDEA
    private static final boolean SLOW_MODE = true;

    // растёт при углублении рекурсии; после StackOverflowError хранит пиковую глубину
    private static int maxDepth = 0;

    public static void main(String[] args) {

        System.out.println("=== JVM MEMORY DEMO ===");
        printMemory("1. Память при старте программы");
        sleep(SLOW_MODE ? 1500 : 0);   // время открыть график памяти в IDEA

        System.out.println("\n=== HEAP DEMO ===");

        List<byte[]> data = new ArrayList<>();

        // Создаем примерно 50 MB объектов в Heap
        for (int i = 1; i <= 500; i++) {
            byte[] block = new byte[1024 * 1024]; // 1 MB
            data.add(block);
            if (i % 10 == 0) {
                printMemory("После выделения " + i + " MB");
            }
            sleep(SLOW_MODE ? 200 : 0);   // 50 MB растут примерно 10 секунд
        }

        printMemory("2. После заполнения Heap");
        System.out.println("\nУдаляем ссылки на объекты...");
        data.clear();
        System.gc();
        sleep(SLOW_MODE ? 2000 : 1000);   // пауза, чтобы увидеть падение на графике
        printMemory("3. После очистки объектов и вызова GC");
        System.out.println("\n=== STACK DEMO ===");

        try {
            recursiveMethod(1);
        } catch (StackOverflowError e) {

            System.out.println();
            System.out.println("StackOverflowError!");
            System.out.println("Стек текущего потока закончился.");

            // JVM обрезает длинные стектрейсы, поэтому кадров может быть
            // меньше реальной глубины рекурсии
            StackTraceElement[] errorStack = e.getStackTrace();
            System.out.println("Кадров в стектрейсе ошибки: " + errorStack.length);
            for (StackTraceElement element : errorStack) {
                // ошибка могла случиться внутри JDK — ищем свой кадр
                if (element.getClassName().equals("MemoryDemo")) {
                    System.out.println("Верхний кадр демо: " + element);
                    break;
                }
            }
        }

        printMemory("4. После StackOverflowError");
        printStackMemory();

        System.out.println("\n=== END ===");
    }


    private static void printMemory(String title) {

        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        long usedMemory = totalMemory - freeMemory;

        System.out.println();
        System.out.println("---- " + title + " ----");
        System.out.println("Max Heap:   " + toMb(maxMemory) + " MB");
        System.out.println("Heap JVM:   " + toMb(totalMemory) + " MB");
        System.out.println("Used Heap:  " + toMb(usedMemory) + " MB");
        System.out.println("Free Heap:  " + toMb(freeMemory) + " MB");
    }


    private static void printStack(String title) {

        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        // пропускаем служебные кадры: getStackTrace/currentThread и сам printStack
        int start = 0;
        while (start < stack.length && stack[start].getClassName().startsWith("java.lang.Thread")) {
            start++;
        }
        if (start < stack.length && stack[start].getMethodName().equals("printStack")) {
            start++;
        }

        System.out.println();
        System.out.println("---- " + title + " ----");
        System.out.println("Кадров в стеке: " + (stack.length - start));
        for (int i = start; i < stack.length; i++) {
            StackTraceElement element = stack[i];
            System.out.println("  кадр " + (i - start + 1) + ": "
                    + element.getMethodName() + " (" + element.getFileName()
                    + ":" + element.getLineNumber() + ")");
        }
    }


    private static void recursiveMethod(int depth) {
        // локальные переменные метода находятся в stack frame
        int number = depth;
        double value = depth * 10.5;
        String text = "depth = " + depth;

        if (depth > maxDepth) {
            maxDepth = depth;
        }

        if (depth <= 3) {
            printStack("Состояние стека при глубине " + depth);
        }

        if (depth % 100 == 0) {
            System.out.println("Глубина рекурсии: " + depth);
            sleep(SLOW_MODE ? 50 : 0);   // рост стека виден в живом темпе
        }

        recursiveMethod(depth + 1);
        // чтобы JVM не могла легко удалить локальные переменные
        System.out.println(number + value + text.length());
    }


    private static long toMb(long bytes) {
        return bytes / 1024 / 1024;
    }


    private static void printStackMemory() {

        System.out.println();
        System.out.println("---- Память стека ----");
        System.out.println("Глубина на момент переполнения: " + maxDepth + " кадров");

        long stackSize = stackSizeBytes();
        if (stackSize > 0) {
            System.out.println("Размер стека потока (-Xss):  " + toSizeText(stackSize));
            // оценка: весь стек поделили поровну на кадры; реальный кадр
            // меняет размер в зависимости от локальных переменных
            System.out.println("Примерный размер кадра:      " + (stackSize / maxDepth) + " байт");
        } else {
            System.out.println("Размер стека потока: по умолчанию (флаг -Xss не задан)");
        }
    }


    // размер стека из аргументов JVM: -Xss256k, -Xss1m, -Xss1048576, ...
    private static long stackSizeBytes() {

        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.startsWith("-Xss") && arg.length() > 4) {
                try {
                    return parseSize(arg.substring(4));
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }


    private static long parseSize(String value) {

        long multiplier = 1;
        char unit = Character.toLowerCase(value.charAt(value.length() - 1));
        if (unit == 'k') {
            multiplier = 1024;
            value = value.substring(0, value.length() - 1);
        } else if (unit == 'm') {
            multiplier = 1024 * 1024;
            value = value.substring(0, value.length() - 1);
        } else if (unit == 'g') {
            multiplier = 1024 * 1024 * 1024;
            value = value.substring(0, value.length() - 1);
        }
        // число без суффикса — байты
        return Long.parseLong(value) * multiplier;
    }


    private static String toSizeText(long bytes) {

        if (bytes >= 1024 * 1024 && bytes % (1024 * 1024) == 0) {
            return (bytes / 1024 / 1024) + " MB";
        }
        return (bytes / 1024) + " KB";
    }


    private static void sleep(long milliseconds) {

        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}