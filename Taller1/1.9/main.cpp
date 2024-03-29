#include <iostream>
#include <array>
#include "lcgrand.c"
using namespace std;

const int MAX_FILA = 10, BUSES_SIMULADOS = 10, DEMORA_ABORDANDO = 5, MIN_ESPERA_BUS = 20,
  DIST_PASAJERO_INF = 5, DIST_PASAJERO_SUP = 35, DIST_BUS_INF = 240, DIST_BUS_SUP = 360;

int tipo_siguiente_evento, largo_fila, personas_que_llegan, personas_que_abordaron, total_buses;

float reloj, tiempo_ultimo_evento, area_fila, total_tiempo_abordaje, total_tiempo_espera, total_tiempo_espera_buses,
      eventos[4], llegada_pasajeros[MAX_FILA], llegada_buses[BUSES_SIMULADOS];

bool bus_disponible;

int semilla;

float uniforme(int a, int b, int semilla){
    /* Return a U(a,b) random variate. */
    return a + drand48() * (b - a);
}

void initialize(){

  reloj = 0.0;

  bus_disponible = false;
  tiempo_ultimo_evento = 0.0;

  personas_que_llegan = 0;
  personas_que_abordaron = 0;
  total_buses = 0;
  largo_fila = 0;
  area_fila = 0.0;
  total_tiempo_abordaje = 0.0;
  total_tiempo_espera = 0.0;
  total_tiempo_espera_buses = 0.0;

  eventos[0] = uniforme(DIST_PASAJERO_INF, DIST_PASAJERO_SUP, semilla);
  eventos[1] = 1.0e+30;
  eventos[2] = uniforme(DIST_BUS_INF, DIST_BUS_SUP, semilla+1);
  eventos[3] = 1.0e+30;
}


void timing(){
  float min = 1.0e+29;
  tipo_siguiente_evento = -1;

  for(int i = 0;i < 4;i++){
    if(eventos[i] < min){
      tipo_siguiente_evento = i;
      min = eventos[i];
    }
  }

  if(tipo_siguiente_evento < 0){
    std::cout << "No se encontro evento" << endl;
    exit(1);
  }

  reloj = eventos[tipo_siguiente_evento];
}

void update_stats(){

  float ancho = reloj - tiempo_ultimo_evento; 
  tiempo_ultimo_evento = reloj;

  area_fila += largo_fila * ancho;

}


void llegada_pasajero(){

  eventos[0] = reloj + uniforme(DIST_PASAJERO_INF, DIST_PASAJERO_SUP, semilla+2);
  personas_que_llegan++;

  if(largo_fila < MAX_FILA){
    llegada_pasajeros[largo_fila] = reloj;
    largo_fila++;
  }

  if(largo_fila == 0 && bus_disponible == true){
    eventos[1] = reloj + DEMORA_ABORDANDO;
  }

}

void abordaje(){

  total_tiempo_abordaje += DEMORA_ABORDANDO;
  total_tiempo_espera += llegada_pasajeros[0];
  personas_que_abordaron++;
  largo_fila--;
  for(int i = 0; i < 9; i++){
    llegada_pasajeros[i] = llegada_pasajeros[i+1];
  }
  if(largo_fila > 0){
    eventos[1] = reloj + DEMORA_ABORDANDO;
    eventos[3] = 1.0e+30;
  }else {
    eventos[1] = 1.0e+30;
    eventos[3] = reloj + DEMORA_ABORDANDO;
  }
}

void llegada_bus(){

  llegada_buses[total_buses] = reloj;
  eventos[2] = reloj + uniforme(DIST_BUS_INF, DIST_BUS_SUP, semilla+3);

  if(largo_fila > 0) eventos[1] = reloj + DEMORA_ABORDANDO;
  else eventos[4] = reloj + MIN_ESPERA_BUS;

}


void salida_bus(){

  total_tiempo_espera_buses += llegada_buses[total_buses];
  eventos[3] = 1.0e+30;
  total_buses++;
}


void generar_estadisticas(){
  cout << "Tiempo Simulado: " << reloj << endl;
  cout << "Personas que abordan: " << personas_que_abordaron << endl;
  cout << "Tiempo espera medio: " << total_tiempo_espera/personas_que_abordaron << endl;
  cout << "Proporcion de abordaje: " << (double)personas_que_abordaron/personas_que_llegan << endl;
  cout << "Tiempo medio de abordaje: " << total_tiempo_abordaje/BUSES_SIMULADOS << endl;
  cout << "Cantidad media de personas por bus: " << (double)personas_que_abordaron/BUSES_SIMULADOS << endl;
  cout << "Tamaño medio de la fila: " << area_fila/reloj << endl << endl;
}


int main(){

  for(int i = 0; i < 100; i++){

    semilla = (int)(lcgrand(i)*100) + 1;
    srand48(semilla);

    initialize();

    while(total_buses < BUSES_SIMULADOS){
      timing();

      update_stats();

      switch(tipo_siguiente_evento){
        case 0:
          llegada_pasajero();
          break;
        case 1:
          abordaje();
          break;
        case 2:
          llegada_bus();
          break;
        case 3:
          salida_bus();
          break;
      }
    }

    generar_estadisticas();
  }


  return 0;
}
