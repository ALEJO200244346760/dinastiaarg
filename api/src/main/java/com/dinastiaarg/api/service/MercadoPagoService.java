package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoPagoService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    public String crearPreferencia(Producto producto) {
        try {
            MercadoPagoConfig.setAccessToken(accessToken);

            // 1. Creamos el ítem del carrito (joya, bolsa, etc)
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(producto.getMercadoLibreId())
                    .title(producto.getNombre())
                    .description(producto.getDescripcion())
                    .quantity(1)
                    .unitPrice(producto.getPrecio())
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 2. Configuramos la preferencia
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(null) // Aquí pondrás luego las URLs de Vercel (success, failure)
                    .autoReturn("approved")
                    .build();

            // 3. Creamos la preferencia en MP
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            // Retornamos el link de pago (init_point)
            return preference.getInitPoint();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error al crear el pago";
        }
    }
}