package com.becoder.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.becoder.entity.AccountStatus;
import com.becoder.entity.User;
import com.becoder.exception.ResourceNotFoundException;
import com.becoder.exception.SuccessException;
import com.becoder.repository.UserRepository;
import com.becoder.service.HomeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class HomeServiceImpl implements HomeService {

	@Autowired
	private UserRepository userRepo;

	@Override
	public Boolean verifyAccount(Integer userId, String verificationCode) throws Exception {
		log.info("HomeServiceImpl : verifyUserAccount : Execution Start ");
		User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("invalid user"));
		if (user.getStatus().getVerificationCode() == null) {
			log.info("message : Account already verified ");
			throw new SuccessException("Account already verified");
		}

		if (user.getStatus().getVerificationCode().equals(verificationCode)) {
			AccountStatus status = user.getStatus();
			status.setIsActive(true);
			status.setVerificationCode(null);
			userRepo.save(user);
			log.info("message : Account Verifiaction Success");
			return true;
		}
		log.info("HomeServiceImpl : verifyUserAccount : Execution End ");
		return false;
	}

}
