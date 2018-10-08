package Ejercicios;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;




public class EnsambladoraDeRopa {
    static final byte LISTO = 1, FIN_DE_LA_SIMULACION = 2, ENCOLAR = 3;

    static final byte STREAM_SERVIDOR_1 = 1, STREAM_SERVIDOR_2 = 2, STREAM_SERVIDOR_3 = 3, STREAM_SERVIDOR_4 = 4, STREAM_SERVIDOR_5 = 5, TIEMPOS_ENTRE_LLEGADAS = 7,
            PIEZA_DANADA_1 = 8, PIEZA_DANADA_2 = 9;

    static int mediaLlegadas, servidor_1, servidor_2, servidor_3, servidor_4_sinDano, servidor_4_conDano, servidor_5,
            duracioDeLaSimulacion, prendasCompletadas;
    static float probabilidadDanoSaco, probabilidadDanoPantalon, sumatoriaDeTiempoPrendasCompletadas;

    static SimReader reader;
    static SimWriter writer;

    static Queue<Float> colaServidor1;
    static Queue<Float> colaServidor2;
    static Queue<Float> colaServidor3;
    static Queue<Float> colaServidor4Pam;
    static Queue<Float> colaServidor4Cam;
    static Queue<Float> colaServidor5;

    static Resource<Float> servidor1;
    static Resource<Float> servidor2;
    static Resource<Float> servidor3;
    static Resource<Float> servidor4;
    static Resource<Float> servidor5;


    public static void main(String[] args) throws Exception {
        reader = new SimReader("EnsambladoraDeRopa.in");
        writer = new SimWriter("EnsambladoraDeRopa.out");
        colaServidor1 = new Queue<>("Cola del servidor 1");
        colaServidor2 = new Queue<>("Cola del servidor 2");
        colaServidor3 = new Queue<>("Cola del servidor 3");
        colaServidor4Pam = new Queue<>("Cola del servidor 4 de pantalones");
        colaServidor4Cam = new Queue<>("Cola del servidor 4 de camisas");
        colaServidor5 = new Queue<>("Cola del servidor 5");

        servidor1 = new Resource<>("Servidor 1");
        servidor2 = new Resource<>("Servidor 2");
        servidor3 = new Resource<>("Servidor 3");
        servidor4 = new Resource<>("Servidor 4");
        servidor5 = new Resource<>("Servidor 5");

        mediaLlegadas = reader.readInt();
        servidor_1 = reader.readInt();
        servidor_2 = reader.readInt();
        servidor_3 = reader.readInt();
        servidor_4_conDano = reader.readInt();
        servidor_4_sinDano = reader.readInt();
        servidor_5 = reader.readInt();
        duracioDeLaSimulacion = reader.readInt();
        probabilidadDanoPantalon = reader.readFloat();
        probabilidadDanoSaco = reader.readFloat();
        initSimlib();
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
                case LISTO:
                    listo();
                    break;
                case FIN_DE_LA_SIMULACION:
                    report();
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
                eventSchedule(expon(servidor_2,STREAM_SERVIDOR_2),ENCOLAR , 4,1);
            }else{
                colaServidor2.offer(servidor1.getElement());
            }

            if(servidor3.isIdle()){
                colaServidor3.offer(servidor1.getElement());
                colaServidor3.poll();
                servidor3.emplace(servidor1.getElement());
                eventSchedule(expon(servidor_3,STREAM_SERVIDOR_3),ENCOLAR , 4,2);
            }else{
                colaServidor3.offer(servidor1.getElement());
            }
            servidor1.remove();
            if(!colaServidor1.isEmpty()){
                servidor1.emplace(colaServidor1.peek());
                colaServidor1.poll();
                eventSchedule(expon(servidor_1,STREAM_SERVIDOR_1),ENCOLAR , 2,0);
            }
        }else if (eventAttributes[0]==4){
            if(eventAttributes[1]==1){
                colaServidor4Cam.offer(servidor2.getElement());
                servidor2.remove();
                if(!colaServidor2.isEmpty()){
                    servidor2.emplace(colaServidor2.peek());
                    colaServidor2.poll();
                    eventSchedule(expon(servidor_2,STREAM_SERVIDOR_2),ENCOLAR , 4,1);
                }
            }
            if(eventAttributes[1]==2){
                colaServidor4Pam.offer(servidor3.getElement());
                servidor3.remove();
                if(!colaServidor3.isEmpty()){
                    servidor2.emplace(colaServidor3.peek());
                    colaServidor3.poll();
                    eventSchedule(expon(servidor_3,STREAM_SERVIDOR_3),ENCOLAR , 4,2);
                }
            }

            if(!colaServidor4Pam.isEmpty() && !colaServidor4Cam.isEmpty() && servidor4.isIdle()){
                float temp = unifrm(1,10,PIEZA_DANADA_1);
                float temp2 = unifrm(1,20,PIEZA_DANADA_1);
                servidor4.emplace(colaServidor4Cam.peek());
                colaServidor4Cam.poll();
                colaServidor4Pam.poll();

                if( temp <= 1 || temp2 <= 1 ){
                    eventSchedule(expon(servidor_4_conDano,STREAM_SERVIDOR_4),ENCOLAR , 5,0);
                }else {
                    eventSchedule(expon(servidor_4_sinDano,STREAM_SERVIDOR_4),LISTO ,4);
                }
            }
        }else if (eventAttributes[0]==5){

            if (!servidor5.isIdle()){
                colaServidor5.offer(servidor4.getElement());
                colaServidor5.poll();
                servidor5.emplace(servidor4.getElement());
                eventSchedule(expon(servidor_5,STREAM_SERVIDOR_5),LISTO ,5);
            }else {
                colaServidor5.offer(servidor4.getElement());
            }
            servidor4.remove();
            eventSchedule(simTime,ENCOLAR , 4,0);
        }
    }

    static void listo(){
        float temp = 0f;
        if (eventAttributes[0]==4){
            temp = servidor4.getElement();
            servidor4.remove();
            eventSchedule(simTime,ENCOLAR , 4,0);
        }else if(eventAttributes[0]==5){
            temp = servidor5.getElement();
            servidor5.remove();
            if (!colaServidor5.isEmpty()){
                servidor5.emplace(colaServidor5.peek());
                colaServidor5.poll();
                eventSchedule(expon(servidor_5,STREAM_SERVIDOR_5),LISTO ,5);
            }
        }
        prendasCompletadas++;
        sumatoriaDeTiempoPrendasCompletadas += simTime - temp;

    }

    static void report() throws IOException {
        colaServidor1.report(writer);
        colaServidor2.report(writer);
        colaServidor3.report(writer);
        colaServidor4Cam.report(writer);
        colaServidor4Pam.report(writer);
        colaServidor5.report(writer);
        servidor1.report(writer);
        servidor2.report(writer);
        servidor3.report(writer);
        servidor4.report(writer);
        servidor5.report(writer);
        writer.write("El numero de prendas completadas es " + prendasCompletadas+
        "\n el promedio de tiempo por prenda es " + sumatoriaDeTiempoPrendasCompletadas/prendasCompletadas);
    }
}
