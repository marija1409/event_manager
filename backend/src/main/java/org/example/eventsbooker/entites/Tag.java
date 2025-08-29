package org.example.eventsbooker.entites;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

//Primeri tagova: rok, metal, rep (za koncerte), slikanje, kuvanje, vajanje (za radionicu),
// it, startap, netvorking (za konferencije), itd.

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    private Long tagId;
    private String name;
}
