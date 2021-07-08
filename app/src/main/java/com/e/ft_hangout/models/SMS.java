package com.e.ft_hangout.models;

public class SMS {
    private final String message;
    private final String receive;
    private final String read;
    private final String author_id;
    private final String destinataire_id;

    public SMS(String message, String receive, String read, String author_id, String destinataire_id){
        this.message = message;
        this.receive = receive;
        this.read = read;
        this.author_id = author_id;
        this.destinataire_id = destinataire_id;
    }

    public String getMessage(){ return message; }

    public String getReceive() { return receive; }

    public String getRead() { return read; }

    public String getAuthor_id() { return author_id; }

    public String getDestinataire_id() {return destinataire_id;};
}
