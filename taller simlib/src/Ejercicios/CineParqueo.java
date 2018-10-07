package Ejercicios;

import simlib.collection.*;
import simlib.elements.*;
import simlib.io.*;
import static simlib.SimLib.*;

import java.io.IOException;


public class CineParqueo {
    static final byte INICIO_FUNCION = 1, EVENTO_LLEGADA_BUS1_HOTEL2 = 2, FIN_DE_LA_SIMULACION = 3;

    static final byte STREAM_SALA_1 = 1, STREAM_SALA_2 = 2, STREAM_SALA_3 = 3, STREAM_SALA_4 = 4, STREAM_SALA_5 = 5, STREAM_SALA_6 = 6, STREAM_SALA_7 = 7,
            STREAM_SALA_8 = 8, FUNCIONES = 9, PERSONAS_POR_AUTO = 10;

    static int numeroSalas, asientos, valorTriangular1, valorTriangular2, valorTriangular3, tiempoSeparacionFuncion, patronesCancelaPorcentaje,
            asistenciaInf, asistenciaSup, sala1, sala2, sala3, sala4, sala5, sala6, sala7, sala8;

    static float promedioPersonasAuto, desviacionPersonasAuto, duracionDeLaSimulacion;

    static SimReader reader;
    static SimWriter writer;

    static ContinStat parqueadero;

    public static void main(String[] args) throws Exception {

        reader = new SimReader("CineParqueo.in");
        writer = new SimWriter("CineParqueo.out");

        numeroSalas = reader.readInt();
        asientos = reader.readInt();
        valorTriangular1 = reader.readInt();
        valorTriangular2 = reader.readInt();
        valorTriangular3 = reader.readInt();
        tiempoSeparacionFuncion = reader.readInt();
        patronesCancelaPorcentaje = reader.readInt();
        asistenciaInf = reader.readInt();
        asistenciaSup = reader.readInt();
        promedioPersonasAuto = reader.readFloat();
        desviacionPersonasAuto = reader.readFloat();
        duracionDeLaSimulacion = reader.readFloat();

        writer.write("Modelo Del parqueadero de un cine\n\n" +
                "Numero de salas                               " + numeroSalas + "\n" +
                "Asientos por sala                             " + asientos+ "\n" +
                "Porcentaje de patrones que cancela            " + patronesCancelaPorcentaje + "\n" +
                "Duración de la simulación                     " + duracionDeLaSimulacion+ " meses \n\n"
        );
        //pasar los meses a minutos
        duracionDeLaSimulacion = duracionDeLaSimulacion * 43800;

        initSimlib();
        parqueadero = new ContinStat("Parqueadero",0);

        //programar todas las funciones de cine en todas las salas

        double[] temp = new double[8];
        temp[0] = 0;
        temp[1] = tiempoSeparacionFuncion;
        temp[2] = tiempoSeparacionFuncion*2;
        temp[3] = tiempoSeparacionFuncion*3;
        temp[4] = tiempoSeparacionFuncion*4;
        temp[5] = tiempoSeparacionFuncion*5;
        temp[6] = tiempoSeparacionFuncion*6;
        temp[7] = tiempoSeparacionFuncion*7;

        while ( temp[0] < duracionDeLaSimulacion || temp[1] < duracionDeLaSimulacion ||
                temp[2] < duracionDeLaSimulacion || temp[3] < duracionDeLaSimulacion ||
                temp[4] < duracionDeLaSimulacion || temp[5] < duracionDeLaSimulacion ||
                temp[6] < duracionDeLaSimulacion || temp[7] < duracionDeLaSimulacion ){
            temp[0] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_1);
            temp[1] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_2);
            temp[2] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_3);
            temp[3] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_4);
            temp[4] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_5);
            temp[5] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_6);
            temp[6] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_7);
            temp[7] += triag(valorTriangular2,valorTriangular1,valorTriangular2,STREAM_SALA_8);

            eventSchedule(temp[0], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 1);
            eventSchedule(temp[1], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 2);
            eventSchedule(temp[2], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 3);
            eventSchedule(temp[3], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 4);
            eventSchedule(temp[4], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 5);
            eventSchedule(temp[5], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 6);
            eventSchedule(temp[6], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 7);
            eventSchedule(temp[7], INICIO_FUNCION, unifrm(asistenciaInf,asistenciaSup,FUNCIONES), 8);

        }

        eventSchedule(duracionDeLaSimulacion,FIN_DE_LA_SIMULACION);

        do {

            timing();
            //System.out.println(simTime);
            switch (eventType) {
                case INICIO_FUNCION:
                    inicioFuncion();
                    break;
                case FIN_DE_LA_SIMULACION:
                    reporte();
                    break;
            }
                /* If the event just executed was not the end-simulation event (type
                   EVENT_END_SIMULATION), continue simulating.  Otherwise, end the
                   simulation. */
        } while (eventType != FIN_DE_LA_SIMULACION);;

        writer.close();
        reader.close();
    }

    static void inicioFuncion(){
        float asistencia = eventAttributes[0] * 0.8f;
        int numeroAutos = 0;
        double temp = 0;
        while (temp < asistencia){
            temp += Normal(promedioPersonasAuto,desviacionPersonasAuto,PERSONAS_POR_AUTO);
            numeroAutos++;
        }

        if (eventAttributes[1] == 1){
            if (sala1 != 0){
                parqueadero.record(parqueadero.getValue() - sala1);
            }
            sala1 = numeroAutos;
        }else  if (eventAttributes[1] == 2){
            if (sala2 != 0){
                parqueadero.record(parqueadero.getValue() - sala2);
            }
            sala2 = numeroAutos;
        }else if (eventAttributes[1] == 3){
            if (sala3 != 0){
                parqueadero.record(parqueadero.getValue() - sala3);
            }
            sala3 = numeroAutos;
        }else if (eventAttributes[1] == 4){
            if (sala4 != 0){
                parqueadero.record(parqueadero.getValue() - sala4);
            }
            sala4 = numeroAutos;
        }else if (eventAttributes[1] == 5){
            if (sala5 != 0){
                parqueadero.record(parqueadero.getValue() - sala5);
            }
            sala5 = numeroAutos;
        }else if (eventAttributes[1] == 6){
            if (sala6 != 0){
                parqueadero.record(parqueadero.getValue() - sala6);
            }
            sala6 = numeroAutos;
        }else if (eventAttributes[1] == 7){
            if (sala7 != 0){
                parqueadero.record(parqueadero.getValue() - sala7);
            }
            sala7 = numeroAutos;
        }else if (eventAttributes[1] == 8){
            if (sala8 != 0){
                parqueadero.record(parqueadero.getValue() - sala8);
            }
            sala8 = numeroAutos;
        }

        parqueadero.record(parqueadero.getValue() + numeroAutos);
    }

    static void reporte() throws IOException {

        parqueadero.report(writer);
    }
}
