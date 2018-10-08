package Ejercicios;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;




public class EnsambladoraDeRopa {
    static final byte CAMBIO_ESTACION = 1, FIN_DE_LA_SIMULACION = 2, ENCOLAR = 3;

    static final byte STREAM_SERVIDOR_1 = 1, STREAM_SERVIDOR_2 = 2, STREAM_SERVIDOR_3 = 3, STREAM_SERVIDOR_4 = 4, STREAM_SERVIDOR_5 = 5, TIEMPOS_ENTRE_LLEGADAS = 7,
            PIEZA_DANADA_1 = 8, PIEZA_DANADA_2 = 9;

    static int mediaLlegadas, servidor_1, servidor_2, servidor_3_sinDano, servidor_3_conDano, servidor_4_sinDano, servidor_4_conDano, servidor_5,
            duracioDeLaSimulacion;
    static float probabilidadDanoSaco, probabilidadDanoPantalon;

    static SimReader reader;
    static SimWriter writer;

    static Queue<Float> colaServidor1;
    static Queue<Float> colaServidor2;
    static Queue<Float> colaServidor3;
    static Queue<Float> colaServidor4;
    static Queue<Float> colaServidor5;

    static Resource<Float> servidor1;
    static Resource<Float> servidor2;
    static Resource<Float> servidor3;
    static Resource<Float> servidor4;
    static Resource<Float> servidor5;


    public static void main(String[] args) throws Exception {
        reader = new SimReader("EnsambladoraDeRopa.in");
        writer = new SimWriter("CineParqueo.out");


        mediaLlegadas = reader.readInt();
        servidor_1 = reader.readInt();
        servidor_2 = reader.readInt();
        servidor_3_conDano = reader.readInt();
        servidor_3_sinDano = reader.readInt();
        servidor_4_conDano = reader.readInt();
        servidor_4_sinDano = reader.readInt();
        servidor_5 = reader.readInt();
        duracioDeLaSimulacion = reader.readInt();
        probabilidadDanoPantalon = reader.readFloat();
        probabilidadDanoSaco = reader.readFloat();

        writer.write("Modelo De la ensambladora de ropa \n\n");
        duracioDeLaSimulacion = duracioDeLaSimulacion*60;
        eventSchedule(duracioDeLaSimulacion,FIN_DE_LA_SIMULACION);
        double temp = 0;
        while (temp < duracioDeLaSimulacion){
            temp += expon(mediaLlegadas,TIEMPOS_ENTRE_LLEGADAS);
            eventSchedule(temp,ENCOLAR,1,0);
        }

        do {

            timing();
            //System.out.println(simTime);
            switch (eventType) {
                case ENCOLAR:
                    encolar();
                    break;
                case CAMBIO_ESTACION:
                    break;
                case FIN_DE_LA_SIMULACION:
                    break;
            }
        } while (eventType != FIN_DE_LA_SIMULACION);;

        writer.close();
        reader.close();

    }

    static void encolar(){
        if(eventAttributes[0]==1){
            if(servidor1.isIdle()){
                colaServidor1.offer(simTime);
                colaServidor1.poll(); //solo es para que aparezca en las estadisticas
                servidor1.emplace(simTime);
                eventSchedule(expon(servidor_1,STREAM_SERVIDOR_1),ENCOLAR , 2,0);
            }else {
                colaServidor1.offer(simTime);
            }
        }else if(eventAttributes[0]==2){
            if(servidor2.isIdle()){
                colaServidor2.offer(servidor1.getElement());
                colaServidor2.poll();
                servidor2.emplace(servidor1.getElement());
                servidor1.remove();
                if(!colaServidor1.isEmpty()){
                    servidor1.emplace(colaServidor1.peek());
                    colaServidor1.poll();
                    eventSchedule(expon(servidor_1,STREAM_SERVIDOR_1),ENCOLAR , 2,0);
                }
                eventSchedule(expon(servidor_2,STREAM_SERVIDOR_2),ENCOLAR , 3,0);
                eventSchedule(expon(servidor_2,STREAM_SERVIDOR_2),ENCOLAR , 3,0);
            }
        }
    }

}
