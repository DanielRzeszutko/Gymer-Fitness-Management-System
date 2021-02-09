package com.gymer.api.slot;

import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.slot.entity.SlotDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/partners/{partnerId}/slots")
public class SlotController {

	private final SlotService slotService;
	private final PartnerService partnerService;

	@Autowired
	public SlotController(SlotService slotService, PartnerService partnerService) {
		this.slotService = slotService;
		this.partnerService = partnerService;
	}

	@GetMapping
	public Iterable<SlotDTO> getAllSlots(@PathVariable Long partnerId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		return partner.getSlots().stream().map(slot -> convertToSlotDTO(slot, partnerId)).collect(Collectors.toList());
	}

	@PostMapping
	public void addSlotToPartner(@RequestBody SlotDTO slotDTO, @PathVariable Long partnerId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		partner.getSlots().add(convertToSlot(slotDTO));
		partnerService.updatePartner(partner);
	}

	@GetMapping("/{slotId}")
	public SlotDTO getSlotById(@PathVariable Long partnerId, @PathVariable Long slotId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Slot> slots = partner.getSlots();
		for (Slot slot : slots) {
			if (slot.getId().equals(slotId)){
				return convertToSlotDTO(slot, partnerId);
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	@PutMapping("/{slotId}")
	public void updateSlotById(@RequestBody SlotDTO slotDTO, @PathVariable Long partnerId, @PathVariable Long slotId) {
		if (!slotDTO.getId().equals(slotId)) throw new ResponseStatusException(HttpStatus.CONFLICT);

		Partner partner = partnerService.getPartnerById(partnerId);
		List<Slot> slots = partner.getSlots();
		for (Slot slot : slots) {
			if (slot.getId().equals(slotId)){
				slotService.updateSlot(convertToSlot(slotDTO));
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	@DeleteMapping("/{slotId}")
	public void deleteSlot(@PathVariable Long partnerId, @PathVariable Long slotId) {
		Partner partner = partnerService.getPartnerById(partnerId);
		List<Slot> slots = partner.getSlots();
		for (Slot slot : slots) {
			if (slot.getId().equals(slotId)){
				slotService.deleteSlot(slot);
			}
		}
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
	}

	private SlotDTO convertToSlotDTO(Slot slot, Long partnerId) {
		Links usersLinks = Links.of(slot.getUsers().stream().map(
				user -> Link.of("/users/" + user.getId())).collect(Collectors.toList()));
		Link employeeLink = Link.of("/partners/" + partnerId + "/employees/" + slot.getEmployee().getId());
		return new SlotDTO(slot.getId(),
				slot.getDate(),
				slot.getStartTime(),
				slot.getEndTime(),
				usersLinks,
				employeeLink,
				slot.isPrivate()
		);
	}

	private Slot convertToSlot(SlotDTO slotDTO) {
		Slot slot = slotService.getSlotById((slotDTO.getId()));
		slot.setDate(slotDTO.getDate());
		slot.setStartTime(slotDTO.getStartTime());
		slot.setEndTime(slotDTO.getEndTime());
		slot.setPrivate(slotDTO.isPrivate());
		return slot;
	}

}
