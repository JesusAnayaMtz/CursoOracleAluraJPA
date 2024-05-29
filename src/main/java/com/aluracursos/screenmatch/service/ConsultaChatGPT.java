package com.aluracursos.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

    public class ConsultaChatGPT {
        public static String obtenerTraduccion(String texto) {
            //introcuimos nuestra apikey
            OpenAiService service = new OpenAiService("sk-proj-mL8zzNUROimvpoV0DteYT3BlbkFJ7t5ZFXYhynwNv6jYanN7");

            CompletionRequest requisicion = CompletionRequest.builder()  //metodos de nuestar dependencia
                    .model("gpt-3.5-turbo-instruct")  //este esta en la doc. ya que puede cambiar para el uso de la requisicion
                    .prompt("traduce a español el siguiente texto: " + texto)  //lo que vamos a pasar en el texto
                    .maxTokens(1000)  //maximo caracteres a traducir
                    .temperature(0.7)  //vaiaciones que puede tener entre una respuesta y otra
                    .build();

            var respuesta = service.createCompletion(requisicion);  //obtenemos la respuesta de la requsicion
            return respuesta.getChoices().get(0).getText();  //nos retorna el primer resultado con get
        }
    }


