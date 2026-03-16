package com.dinastiaarg.api.service;

import com.dinastiaarg.api.model.Producto;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
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

            // 1. Ítem del producto
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(producto.getMercadoLibreId())
                    .title(producto.getNombre())
                    .quantity(1)
                    .unitPrice(producto.getPrecio())
                    .currencyId("ARS")
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 2. URLs de retorno (Vercel)
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success("https://dinastiaarg.vercel.app/pago-exitoso")
                    .pending("https://dinastiaarg.vercel.app/pago-pendiente")
                    .failure("https://dinastiaarg.vercel.app/pago-error")
                    .build();

            // 3. Configuración completa
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .backUrls(backUrls) // Agregamos las URLs
                    .autoReturn("approved") // Si el pago es exitoso, vuelve solo
                    .build();

            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return preference.getInitPoint();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }
}