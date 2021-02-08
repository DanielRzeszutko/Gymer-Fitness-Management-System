package com.gymer.api.slot;

import com.gymer.api.slot.entity.Slot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SlotService {

	private final SlotRepository slotRepository;

	@Autowired
	public SlotService(SlotRepository slotRepository) {
		this.slotRepository = slotRepository;
	}

	public Slot getSlotById(Long slotId) {
		return slotRepository.findById(slotId).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	public void updateSlot(Slot slot) {
		slotRepository.save(slot);
	}

	public void deleteSlot(Slot slot){
		slotRepository.delete(slot);
	}

}
