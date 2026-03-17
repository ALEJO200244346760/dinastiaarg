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
            // 1. Configurar tu Access Token (USÁ EL MISMO QUE USAMOS PARA MELI)
            MercadoPagoConfig.setAccessToken("APP_USR-3616307332149511-031714-6880053916962f3a69a234327576563d-1297120798");

            // 2. Crear el ítem de la joya
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .title(titulo)
                    .quantity(cantidad)
                    .unitPrice(new BigDecimal(precio.toString()))
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 3. Configurar URLs de retorno (A dónde vuelve el cliente tras pagar)
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://dinastiaarg.vercel.app/pago-exitoso")
                    .pending("https://dinastiaarg.vercel.app/pago-pendiente")
                    .failure("https://dinastiaarg.vercel.app/pago-fallido")
                    .build();

            // 4. Crear la preferencia
            PreferenceClient client = new PreferenceClient();
            PreferenceRequest request = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls)
                    .autoReturn("approved") // Vuelve solo a la web si el pago es exitoso
                    .build();

            Preference preference = client.create(request);

            // 5. Retornar el Init Point (el link de la pasarela de pago)
            return preference.getInitPoint();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}