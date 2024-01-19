import { Fragment, useEffect, useState } from "react";
import avatar from "../../../styles/images/avatar.jpg";
import "./_header.scss";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLE_NAMES } from "../../../constants/role_constants";
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
				<div className="pd-left-240 ps-30" id="header-col-1"></div>
				<div className="d-flex align-items-center">
					<div className="bell-icon">
						<i className="bi bi-bell side-nav-toggle"></i>
					</div>
					<div className="dropdown">
						<div
							className="user-dropdown"
							data-bs-toggle="dropdown"
							aria-expanded="false"
						>
							<span className="user-pic">
								<img src={avatar} />
							</span>
							<span className="user-name">
								{userInfo?.name}
								<i className="bi bi-chevron-down"></i>
							</span>
							<span className="font-size-12">{USER_ROLE_NAMES[userInfo?.roleIds[0]]}</span>
						</div>
						<ul className="dropdown-menu">
							<li>
								<a className="dropdown-item" href="#">
									Action
								</a>
							</li>
							<li>
								<a className="dropdown-item" href="#">
									Another action
								</a>
							</li>
							<li>
								<a className="dropdown-item" href="#">
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
