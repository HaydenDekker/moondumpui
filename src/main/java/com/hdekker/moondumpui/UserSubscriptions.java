package com.hdekker.moondumpui;

import java.util.List;

public class UserSubscriptions {

		final String email;
		final List<String> keys;
		public UserSubscriptions(String email, List<String> keys) {
			super();
			this.email = email;
			this.keys = keys;
		}
		public String getEmail() {
			return email;
		}
		public List<String> getKeys() {
			return keys;
		}
	
}
