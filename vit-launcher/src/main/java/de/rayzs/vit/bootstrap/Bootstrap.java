package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.processes.AssetPreparer;

public class Bootstrap {

    public static void main(String[] args) {

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);

        final AssetPreparer prep = new AssetPreparer(api);


    }
}
