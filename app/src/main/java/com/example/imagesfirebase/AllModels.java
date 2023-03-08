package com.example.imagesfirebase;

import java.util.List;

public class AllModels {
    List<String> images;

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public AllModels(List<String> images) {
        this.images = images;
    }
}
