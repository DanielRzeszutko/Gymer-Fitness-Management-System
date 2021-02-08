package com.gymer.api.slot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotService {

	private final SlotRepository slotRepository;

	@Autowired
	public SlotService(SlotRepository slotRepository) {
		this.slotRepository = slotRepository;
	}
}
