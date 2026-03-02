package de.rayzs.vit.api.request;

public enum RequestDest {

        API             ("https://valorant-api.com/v1/"),

        LOCAL           ("https://127.0.0.1:%d/"),               // port
        PD              ("https://pd.%s.a.pvp.net/"),            // region 1
        SHARED          ("https://shared.%s.a.pvp.net/"),        // region 1
        GLZ             ("https://glz-%s.%s.a.pvp.net/");        // region 2, region 1
        
        
        private final String unformattedUrl;
        private String formattedUrl;
        RequestDest(final String url) {
            this.unformattedUrl = url;

            if (!this.unformattedUrl.contains("%")) {
                this.formattedUrl = this.unformattedUrl;
            }
        }

        /**
         * Updates the format placeholders %s/%d
         * since they can only be received after
         * initial connections have been made.
         * 
         * @param replacements Replacements for the unformatted url.
         */
        public void update(final Object... replacements) {
            this.formattedUrl = String.format(unformattedUrl, replacements);
        }

        /**
         * Gets the unformatted url.
         * Basically the unchanged and raw url.
         * 
         * @return Unformatted url.
         */
        public String getUnformattedUrl() {
            return this.unformattedUrl;
        }

        /**
         * Builds the URL. No need to start with a slash.
         * Initial slash is already provided!
         * 
         * <pre>
         *  {@code
         *      Destination.LOCAL.from("test/apple");
         *      // => https://127.0.0.1:?/test/apple
         *  }
         * </pre>
         * 
         * @param urlPath Ongoing path.
         * @return Returns completed path.
         */
        public String from(final String urlPath) {
            if (this.formattedUrl == null) {
                // In case the url hasn't been updated yet,
                // it will stop the call and report an error.
                throw new IllegalStateException("URL hasn't been formatted yet. Please do that first!");
            }
            
            return this.formattedUrl + urlPath;
        }
    }