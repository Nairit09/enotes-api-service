package com.becoder.service;

import com.becoder.dto.PasswordChngRequest;

public interface UserService {

	public Boolean changePassword(PasswordChngRequest passwordRequest);
}
