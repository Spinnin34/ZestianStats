package p.zestianStats.Utils;

import java.sql.Connection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatabaseQueue {

    private final BlockingQueue<DatabaseTask> queue;
    private final Thread workerThread;
    private final AtomicBoolean running;

    public DatabaseQueue() {
        this.queue = new LinkedBlockingQueue<>(100); // Límite de 100 tareas en la cola
        this.running = new AtomicBoolean(true);
        this.workerThread = new Thread(this::processQueue);
        this.workerThread.start();
    }

    public void addTask(DatabaseTask task) {
        if (running.get()) {
            if (!queue.offer(task)) { // Intentar añadir la tarea a la cola sin bloquear
                System.err.println("La cola de la base de datos está llena. La tarea ha sido rechazada.");
            }
        }
    }

    private void processQueue() {
        while (running.get() || !queue.isEmpty()) {
            try {
                DatabaseTask task = queue.poll(); // Tomar una tarea de la cola
                if (task != null) {
                    boolean success = false;
                    int attempts = 0;
                    while (!success && attempts < 3) { // Reintentar hasta 3 veces si hay un error
                        try {
                            task.run();
                            success = true;
                        } catch (Exception e) {
                            attempts++;
                            System.err.println("Error al ejecutar la tarea en la base de datos. Intento " + attempts + " de 3.");
                            e.printStackTrace();
                        }
                    }

                    if (!success) {
                        System.err.println("La tarea ha fallado después de 3 intentos.");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log cualquier excepción que ocurra en el hilo de procesamiento
            }
        }
    }

    public void shutdown() {
        running.set(false);
        workerThread.interrupt();
        try {
            workerThread.join(); // Esperar a que el hilo termine
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restaurar el estado de interrupción
        }
    }
}
