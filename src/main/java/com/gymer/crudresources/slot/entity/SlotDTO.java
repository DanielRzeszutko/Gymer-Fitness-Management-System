package com.gymer.crudresources.slot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Date;
import java.sql.Time;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class SlotDTO extends RepresentationModel<SlotDTO> {

    private Long id;
    private String description;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String slotType;
    private boolean isPrivate;
    private Integer size;

    public SlotDTO(Slot slot) {
        this.id = slot.getId();
        this.description = slot.getDescription();
        this.date = slot.getDate();
        this.startTime = slot.getStartTime();
        this.endTime = slot.getEndTime();
        this.slotType = slot.getSlotType();
        this.isPrivate = slot.isPrivate();
        this.size = slot.getSize();
    }

}
