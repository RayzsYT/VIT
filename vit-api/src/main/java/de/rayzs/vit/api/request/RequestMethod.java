package de.rayzs.vit.api.request;

public enum RequestMethod {

        GET     ("GET"), 
        POST    ("POST"),
        PUT    ("PUT");
        
        
        private final String name;
        RequestMethod(final String name) {
            this.name = name; 
        }

        public String getName() {
            return name;
        }
    }