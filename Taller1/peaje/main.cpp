#include <iostream>
#include <math.h>
//#include "lcgrand.h"
#include <stdlib.h>
#include <time.h>
using namespace std;

const int CANT_CASETAS = 4, COSTO_NORMAL = 8, COSTO_PESADO = 12, MAX_FILA = 1000, //Usar max fila para no ponernos a usar listas xD
        PESADO = 0, NORMAL = 1; 
  
const float TIEMPO_SIMULADO = 24.0*3600.0, DIST_CLIENTES = 3600.0/40.0, DIST_DEMORA = 80.0, PROB_TRANS_PESADO = 0.3, PROB_ABANDONAR = 0.2;

int tipo_siguiente_evento, largo_fila, viajeros_atendidos, transporte_pesado_atendido, casetas_ocupadas, tipo_viajeros[MAX_FILA];

float reloj, tiempo_ultimo_evento, beneficios, area_fila, area_casetas_ocupados, total_tiempo_espera, 
    eventos[2], llegada_viajeros[MAX_FILA], viajero_en_caseta[CANT_CASETAS];


int semilla;

float exponencial(float media, int semilla){
    
    return -media * log(drand48());
}


void initialize(){
  
  srand48(semilla);

  reloj = 0.0;

  for(int i = 0; i < CANT_CASETAS; i++) viajero_en_caseta[i] = 1.0e+30;
  tiempo_ultimo_evento = 0.0;

  beneficios = 0.0;
  viajeros_atendidos = 0;
  transporte_pesado_atendido = 0;
  casetas_ocupadas = 0;
  area_fila = 0.0;
  area_casetas_ocupados = 0.0;
  total_tiempo_espera = 0.0;
  largo_fila = 0;
  

  eventos[0] = exponencial(DIST_CLIENTES, semilla);
  eventos[1] = 1.0e+30;
}


void timing(){
  float min = 1.0e+29;
  tipo_siguiente_evento = -1;

  for(int i = 0; i < 2; i++){
    if(eventos[i] < min){
      tipo_siguiente_evento = i;
      min = eventos[i];
    }
  }

  if(tipo_siguiente_evento < 0){
    cout << "No se encontro evento" << endl;
    exit(1);
  }

  reloj = eventos[tipo_siguiente_evento];
}

void update_stats(){

  float ancho = reloj - tiempo_ultimo_evento; //Se llama asi porque es el ancho del dichoso rectangulo :V
  tiempo_ultimo_evento = reloj;

  area_fila += largo_fila * ancho;
  area_casetas_ocupados += casetas_ocupadas * ancho;

}


void llegada_viajero(){

  eventos[0] = reloj + exponencial(DIST_CLIENTES, semilla+1);
  
  if(casetas_ocupadas < CANT_CASETAS){
    
    int caseta_disponible = -1;
    
    for(int i = 0; i < CANT_CASETAS; i++){
      if(viajero_en_caseta[i] > 1.0e+29){
        caseta_disponible = i;
        break;
      }
    }
    
    if(caseta_disponible < 0){
      cout << " Error para encontrar una caseta" << endl;
      exit(1);
    }
    
    tipo_viajeros[caseta_disponible] = drand48() < PROB_TRANS_PESADO? PESADO: NORMAL;
    viajero_en_caseta[caseta_disponible] = reloj + exponencial(DIST_DEMORA, semilla+2);
    
    if(viajero_en_caseta[caseta_disponible] < eventos[1])
        eventos[1] = viajero_en_caseta[caseta_disponible];
    
    casetas_ocupadas++;
      
  }else{
    
    if(drand48() > PROB_ABANDONAR){
      if(largo_fila < MAX_FILA){
      
        llegada_viajeros[largo_fila] = reloj;
        largo_fila++;
        
      }else{
        cout << " Hay que subirle al maximo Fila" << endl;
        exit(1);
      }
    }
    
  }

}

void despacho_viajero(){
  
  viajeros_atendidos++;
  
  int caseta_actual = -1;

  for(int i = 0; i < CANT_CASETAS; i++){
    if(viajero_en_caseta[i] == reloj){
      caseta_actual = i;
      break;
    }
  }

  if(caseta_actual < 0){
    cout << "Error con la asignacion de casetas" << endl;
    exit(1);
  }
  
  if(tipo_viajeros[caseta_actual] == NORMAL) beneficios += COSTO_NORMAL;
  else{
    transporte_pesado_atendido++;
    beneficios += COSTO_PESADO;
  } 
  
  if(largo_fila > 0){
    
    total_tiempo_espera += reloj - llegada_viajeros[0];
    
    viajero_en_caseta[caseta_actual] = reloj + exponencial(DIST_DEMORA, semilla+5);
    tipo_viajeros[caseta_actual] = drand48() < PROB_TRANS_PESADO? PESADO: NORMAL;
    
    largo_fila--;
    for(int i = 0; i < largo_fila; i++){
      llegada_viajeros[i] = llegada_viajeros[i+1];
    }
    
  }else{
    viajero_en_caseta[caseta_actual] = 1.0e+30;
    casetas_ocupadas--;
  }
  
  float min = 1.0e+30;
    for(int i = 0; i < CANT_CASETAS; i++){
      if(viajero_en_caseta[i] < min) min = viajero_en_caseta[i];
    }
    eventos[1] = min;
 
}


void generar_estadisticas(){
  cout << "Tiempo Simulado: " << reloj << endl;
  cout << "Viajeros Atendidos: " << viajeros_atendidos << endl;
  cout << "Tiempo espera medio: " << total_tiempo_espera/viajeros_atendidos << endl;
  cout << "Transporte pesado atendido " << transporte_pesado_atendido << endl;
  cout << "Beneficios Totales: " << beneficios << endl;
  cout << "Beneficio medio por viajero: " << (double)beneficios/viajeros_atendidos << endl << endl;
}


int main(){

  for(int i = 0; i < 10; i++){

    semilla = (int)(drand48()*100) + 1;

    initialize();

    while(reloj < TIEMPO_SIMULADO){
      timing();

      update_stats();

      switch(tipo_siguiente_evento){
        case 0:
          llegada_viajero();
          break;
        case 1:
          despacho_viajero();
          break;
      }
    }

    generar_estadisticas();
  }


  return 0;
}

/* g++ -Wall principal.cpp biblioteca1.cpp -o salida //Para compilar
  https://plot.ly/create/box-plot/#/    //Para hacer el box
 */
