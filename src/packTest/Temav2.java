package packTest;

import java.io.IOException;
import java.util.Scanner;
import java.awt.Desktop;
import java.io.File;
import java.io.PipedOutputStream;
import java.io.PipedInputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import packWork.Manipulation;

public class Temav2 {
    public static void main(String[] args) throws InterruptedException {
        final PC pc = new PC();

        // Create producer thread
        Thread t1 = new Thread(() -> {
            try {
                pc.produce();
            } catch (InterruptedException e) {
                System.out.println("Nu am putut rula threadul Produce");

            }
        });

        // Create consumer thread
        Thread t2 = new Thread(() -> {
            try {
                pc.consume();
            } catch (InterruptedException e) {
                System.out.println("Nu am putut rula threadul Consume");
                return;
            }
        });

        // Start both threads
        t1.start();
        t2.start();

        // t1 finishes before t2
        t1.join();
        t2.join();

        // dupa ce se terminat threadurile incepem procesarea
        try {
            pc.procesare();
        } catch (IOException e) {
            System.out.println("Nu am putut incepe procesarea imaginii");
        }
        final PipedInputStream input = new PipedInputStream();
        final PipedOutputStream output = new PipedOutputStream();

        try {
            input.connect(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class PC {
        Manipulation original;  // variabila asupra careia o sa facem modificarile
        String read_path;       // variabila pentru citirea locatiei pozei
        long startTime;
        int factor;
        boolean produced = false;  // Flag to track whether the producer has produced data

        // bloc de initializare
        {
            original = null;        // variabila asupra careia o sa facem modificarile
            read_path = null;       // variabila pentru citirea locatiei pozei
            factor = 0;
        }

        public synchronized void produce() throws InterruptedException {
            System.out.println("Incepem thread-ul produce");
            // citim de la tastatura numele imaginii si calculam timpul de citire
            try {
                Scanner sc = new Scanner(System.in);
                System.out.println("Introduceti imaginea sursa:");
                read_path = sc.nextLine();
                read_path += ".bmp";
                //citire din fisier a imaginii sursa
                original = new Manipulation(read_path);
                startTime = System.nanoTime();
                try {
                    original.read(read_path);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Timp citire:    " + (double)(System.nanoTime() - startTime)/1000000 + " milisecunde");

            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
                System.exit(0);
            }
            produced = true;  // folosim acest flag sa stim cand am terminat de citit
            // activam threadul consumer
            notify();
            System.out.println("Thread-ul produce s-a terminat\n");

            Thread.sleep(1000);
        }

        public synchronized void consume() throws InterruptedException {
            System.out.println("Incepem thread-ul consume");
            while (!produced) {
                wait();
            }

            // citim factorul de modificare
            System.out.print("Dati un factor de modificare [-1->-255(mai intunecat),1->255(mai luminat)]:");
            Scanner sc = new Scanner(System.in);
            factor = sc.nextInt();

            notify();

            System.out.println("Thread-ul Consume s-a terminat\n");
            Thread.sleep(1000);
        }

        public void procesare() throws InterruptedException, IOException {
            System.out.println("Incepem procesarea imaginii");
            startTime = System.nanoTime(); // memoram timpul la care incepem procesarea imaginii
            // procesam imaginea
            original.AdjustBrightness(factor);
            // afisam timpul de procesare
            System.out.println("Timp procesare:   " + (double) (System.nanoTime() - startTime) / 1000000 + " milisecunde");
            try {
                // calculam timpul de scriere
                startTime = System.nanoTime();
                original.write(original);
                System.out.println("Timp scriere:   " + (double) (System.nanoTime() - startTime) / 1000000 + " milisecunde");
            } catch (IOException ex) {
                System.out.println("Nu am putut scrie imaginea");
            }

            // deschidere fisier
            File g = new File("modified_" + read_path);
            Desktop dt = Desktop.getDesktop();
            try {
                dt.open(g);
            } catch (IOException e) {
                System.out.println("Nu am putut deschide");
            }
            System.out.println("Am terminat procesarea imaginii");
        }
        public void writeToPipe(OutputStream outputStream) throws IOException {
            // Verificăm dacă producătorul a terminat de produs date
            if (!produced) {
                throw new IllegalStateException("Producerul nu a terminat de produs date");
            }
            System.out.println("Incepem scrierea in pipe");

            // Scrie imaginea procesată în PipedOutputStream
            ImageIO.write(original.Get_img(), "bmp", outputStream);
            System.out.println("Incepem scrierea in pipe");
            // Închide PipedOutputStream pentru a semnala că datele au fost scrise
            outputStream.close();

            System.out.println("Scrierea in pipe s-a terminat");
        }
    }
}
