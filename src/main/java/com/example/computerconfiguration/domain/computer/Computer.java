package com.example.computerconfiguration.domain.computer;

import com.example.computerconfiguration.domain.AttributeInfo;
import com.example.computerconfiguration.domain.Configuration;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Computer extends Configuration {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    public int uso;
    public int gabinete;
    public int processador;
    public int driveOptico;
    public int placaVideo;
    public int memoria;
    public int hdd;
    public int ssd;

    // número de quantas vezes a configuração foi efetuada
    public int replicacoes = 1;

    private static final HashMap<String, AttributeInfo> attributeInfo = new HashMap<>();

    static {
        attributeInfo.put("uso", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("gabinete", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("processador", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("driveOptico", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("placaVideo", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("memoria", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("hdd", new AttributeInfo(0.125, "EIB"));
        attributeInfo.put("ssd", new AttributeInfo(0.125, "EIB"));
    }

    public Computer(List<Integer> componentes) {
        super(attributeInfo);
        this.uso = componentes.get(0);
        this.gabinete = componentes.get(1);
        this.processador = componentes.get(2);
        this.driveOptico = componentes.get(3);
        this.placaVideo = componentes.get(4);
        this.memoria = componentes.get(5);
        this.hdd = componentes.get(6);
        this.ssd = componentes.get(7);
        this.replicacoes = componentes.get(8);
    }

    public Computer() {
        super(attributeInfo);
    }

    @Override
    public String toString() {
        return "Computer{" +
                "uso=" + uso +
                ", gabinete=" + gabinete +
                ", processador=" + processador +
                ", driveOptico=" + driveOptico +
                ", placaVideo=" + placaVideo +
                ", memoria=" + memoria +
                ", hdd=" + hdd +
                ", ssd=" + ssd +
                '}';
    }

    public String getUsoString() {
        switch (uso) {
            case 1 : {
                return "básico";
            }
            case 2 : {
                return "desenvolvimento";
            }
            default : {
                return "games";
            }
        }
    }

    public String getGabineteString() {
        switch (gabinete) {
            case 1: {
                return "mini";
            }
            case 2: {
                return "desktop";
            }
            default: {
                return "gamer";
            }
        }
    }

    public String getProcessadorString() {
        if (processador == 1) {
            return "dual-core";
        }
        return "quad-core";
    }

    public String getDriveOpticoString() {
        if (driveOptico == 1) {
            return "sim";
        }
        return "não";
    }

    public String getPlacaVideoString() {
        if (placaVideo == 1) {
            return "sim";
        }
        return "não";
    }

    public String getMemoriaString() {
        switch (memoria) {
            case 1: {
                return "4GB";
            }
            case 2: {
                return "8GB";
            }
            default: {
                return "16GB";
            }
        }
    }

    public String getHddString() {
        if (hdd == 1) {
            return "sim";
        }
        return "não";
    }

    public String getSsdString() {
        if (ssd == 1) {
            return "sim";
        }
        return "não";
    }

    public static String stringifyAttributeValue(String name, Integer value) {
        String valueStr;
        switch (name) {
            case "uso":
                if (value.equals(1)) {
                    valueStr = "básico";
                } else if (value.equals(2)) {
                    valueStr = "desenvolvimento";
                } else {
                    valueStr = "games";
                }
                break;
            case "gabinete":
                if (value.equals(1)) {
                    valueStr = "mini";
                } else if (value.equals(2)) {
                    valueStr = "desktop";
                } else {
                    valueStr = "gamer";
                }
                break;
            case "memoria":
                if (value.equals(1)) {
                    valueStr = "4GB";
                } else if (value.equals(2)) {
                    valueStr = "8GB";
                } else {
                    valueStr = "16GB";
                }
                break;
            case "processador":
                if (value.equals(1)) {
                    valueStr = "dual-core";
                } else {
                    valueStr = "quad-core";
                }
                break;
            default:
                if (value.equals(1)) {
                    valueStr = "sim";
                } else {
                    valueStr = "não";
                }
                break;
        }
        return valueStr;
    }

    // used by Thymeleaf
    public String getReplicacoesString() {
        return String.valueOf(replicacoes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer computer = (Computer) o;
        return uso == computer.uso && gabinete == computer.gabinete && processador == computer.processador &&
                driveOptico == computer.driveOptico && placaVideo == computer.placaVideo && memoria == computer.memoria
                && hdd == computer.hdd && ssd == computer.ssd;
    }

    public void addReplicacao() {
        replicacoes++;
    }

}
