package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.start.AssetPreparer;

import java.io.IOException;

public class Bootstrap {

    public static void main(String[] args) {

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);



        final AssetPreparer prep = new AssetPreparer(api, process -> {
            try {
                System.out.write(("\r" + process.getPercent() + "%  ").getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
