package com.runescape.util;

import com.runescape.ClientKT;

public final class MouseDetection implements Runnable {

    public final Object syncObject;
    public final int[] coordsY;
    public final int[] coordsX;
    public boolean running;
    public int coordsIndex;
    private ClientKT clientKTInstance;

    public MouseDetection(ClientKT clientKT1) {
        syncObject = new Object();
        coordsY = new int[500];
        running = true;
        coordsX = new int[500];
        clientKTInstance = clientKT1;
    }

    public void run() {
        while (running) {
            synchronized (syncObject) {
                if (coordsIndex < 500) {
                    coordsX[coordsIndex] = clientKTInstance.mouseX;
                    coordsY[coordsIndex] = clientKTInstance.mouseY;
                    coordsIndex++;
                }
            }
            try {
                Thread.sleep(50L);
            } catch (Exception _ex) {
            }
        }
    }
}
