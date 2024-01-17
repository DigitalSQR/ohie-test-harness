import { Fragment, useEffect, useState } from "react";
import avatar from "../styles/images/avatar.jpg";
import "../scss/_header.scss";
import { UserAPI } from "../api/UserAPI";
import { USER_ROLE_NAMES } from "../constants/role_constants";
export default function Header() {
	const [userInfo, setUserInfo] = useState();
	
	useEffect(() => {
		UserAPI.viewUser().then(res => {
            setUserInfo(res);
        }).catch((error)=>{ throw error;})
	}, []);
	return (
		<Fragment>
			<header>
				<div class="pd-left-240 ps-30" id="header-col-1"></div>
				<div class="d-flex align-items-center">
					<div class="bell-icon">
						<i class="bi bi-bell side-nav-toggle"></i>
					</div>
					<div class="dropdown">
						<div
							class="user-dropdown"
							data-bs-toggle="dropdown"
							aria-expanded="false"
						>
							<span class="user-pic">
								<img src={avatar} />
							</span>
							<span class="user-name">
								{userInfo?.name}
								<i class="bi bi-chevron-down"></i>
							</span>
							<span class="font-size-12">{USER_ROLE_NAMES[userInfo?.roleIds[0]]}</span>
						</div>
						<ul class="dropdown-menu">
							<li>
								<a class="dropdown-item" href="#">
									Action
								</a>
							</li>
							<li>
								<a class="dropdown-item" href="#">
									Another action
								</a>
							</li>
							<li>
								<a class="dropdown-item" href="#">
									Something else here
								</a>
							</li>
						</ul>
					</div>
				</div>
			</header>
		</Fragment>
	);
}
