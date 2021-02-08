package com.gymer.api.slot;

import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.slot.entity.SlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners/{partnerId}")
public class SlotController {

	private final SlotService slotService;
	private final PartnerService partnerService;

	@Autowired
	public SlotController(SlotService slotService, PartnerService partnerService) {
		this.slotService = slotService;
		this.partnerService = partnerService;
	}

	@GetMapping("/slots")
	public Iterable<Slot> getAllSlots(@PathVariable Long partnerId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		return partner.getSlots();
	}

	//todo implement other endpoints

	private SlotDTO convertToSlotDTO(Slot slot) {
		List<Link> usersLinks = slot.getUsers().stream().map(
				user -> Link.of("/users/" + user.getId())).collect(Collectors.toList());

		return new SlotDTO(slot.getId(),
				slot.getDate(),
				slot.getStartTime(),
				slot.getEndTime(),
				usersLinks,
				slot.getEmployee(),
				slot.isPrivate()

		);
	}

	private Slot convertToSlot(SlotDTO slotDTO) {
		//todo add convert body
		return null;
	}

}
