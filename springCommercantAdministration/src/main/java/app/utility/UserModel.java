package app.utility;

import data.entitys.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserModel {
	private User user;
	private FilterCmd filter;
//	public UserModel(User user) {		
//		this.user = user;
//		if(this.user!=null)
//			this.filter=new FilterCmd(this.user.getId());
//	}
	public UserModel(User user, FilterCmd filter) {
		this.user = user;
		this.filter = filter;
		if(this.user!=null) {
			if(this.filter!=null)
				this.filter.setUserId(this.user.getId());
			else
				this.filter=new FilterCmd(this.user.getId());
		}
	}
		
}
