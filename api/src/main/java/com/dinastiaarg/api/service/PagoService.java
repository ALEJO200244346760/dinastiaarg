package com.dinastiaarg.api.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagoService {

    public String crearPreferencia(String titulo, Double precio, Integer cantidad) {
        try {
            String token = "APP_USR-3616307332149511-031614-1239c11e12a3e409f03790faf7d193eb-43712155";
            MercadoPagoConfig.setAccessToken(token.trim());

            // 2. Crear el ítem (MeLi es exigente con el formato del precio)
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id("joya-dinastia")
                    .title(titulo)
                    .quantity(1) // Empecemos con 1 para probar
                    .unitPrice(new BigDecimal(precio.toString()))
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 3. URLs de retorno
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://dinastiaarg.vercel.app/")
                    .pending("https://dinastiaarg.vercel.app/")
                    .failure("https://dinastiaarg.vercel.app/")
                    .build();

            // 4. Crear la petición
            PreferenceClient client = new PreferenceClient();
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved")
                    // Esto evita problemas con usuarios que no tienen cuenta
                    .binaryMode(true)
                    .build();

            System.out.println("Enviando preferencia a Mercado Pago para: " + titulo);
            Preference preference = client.create(request);

            // 5. Devolvemos el link de pago
            return preference.getInitPoint();

        } catch (Exception e) {
            // Esto va a imprimir el error REAL en el log de Railway
            System.err.println("ERROR DETALLADO DE MP: " + e.getMessage());
            if (e instanceof com.mercadopago.exceptions.MPApiException) {
                System.err.println("Causa API: " + ((com.mercadopago.exceptions.MPApiException) e).getApiResponse().getContent());
            }
            return "Error al conectar con Mercado Pago";
        }
    }
}