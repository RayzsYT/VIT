package de.rayzs.vit.bootstrap;

import de.rayzs.vit.api.VIT;
import de.rayzs.vit.impl.VITAPIImpl;
import de.rayzs.vit.start.VITStart;

public class Bootstrap {

    public static void main(String[] args) {

        final VITAPIImpl api = new VITAPIImpl();
        VIT.set(api);

        final VITStart start = new VITStart(api);

    }
}
