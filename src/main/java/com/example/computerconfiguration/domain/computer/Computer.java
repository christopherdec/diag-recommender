package com.example.computerconfiguration.domain.computer;

import com.example.computerconfiguration.diagnostic.AttributeInfo;
import com.example.computerconfiguration.domain.CompleteConfiguration;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Computer extends CompleteConfiguration {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long id;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int uso;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int gabinete;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int processador;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int driveOptico;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int placaVideo;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int memoria;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int hdd;

    @AttributeInfo(metric = "EIB", weight = 0.125)
    public int ssd;

    public Computer(List<Integer> componentes) {
        super(componentes.get(8));
        this.uso = componentes.get(0);
        this.gabinete = componentes.get(1);
        this.processador = componentes.get(2);
        this.driveOptico = componentes.get(3);
        this.placaVideo = componentes.get(4);
        this.memoria = componentes.get(5);
        this.hdd = componentes.get(6);
        this.ssd = componentes.get(7);
    }

    public Computer() {
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
                ", replicacoes=" + getReplications() +
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Computer computer = (Computer) o;
        return uso == computer.uso && gabinete == computer.gabinete && processador == computer.processador &&
                driveOptico == computer.driveOptico && placaVideo == computer.placaVideo && memoria == computer.memoria
                && hdd == computer.hdd && ssd == computer.ssd;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uso, gabinete, processador, driveOptico, placaVideo, memoria, hdd, ssd,
                super.getReplications());
    }
}
