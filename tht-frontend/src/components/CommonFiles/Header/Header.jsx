import { Fragment, useEffect, useState } from "react";
import avatar from "../../../styles/images/avatar.jpg";
import "./_header.scss";
import { UserAPI } from "../../../api/UserAPI";
import { USER_ROLE_NAMES } from "../../../constants/role_constants";
import { store } from "../../../store/store";
import { log_out } from "../../../reducers/authReducer";
import { useDispatch } from "react-redux";


export default function Header() {
	const [userInfo, setUserInfo] = useState();
	const dispatch = useDispatch();
	useEffect(() => {
		const userInfo = store.getState().userInfoSlice;
		setUserInfo(userInfo);
	}, []);
	return (
		<Fragment>
			<header>
				<div className="pd-left-240 ps-30" id="header-col-1"></div>
				<div className="d-flex align-items-center">
					{/* <div className="bell-icon">
						<i className="bi bi-bell side-nav-toggle"></i>
					</div> */}
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
							<span className="font-size-12">
								{USER_ROLE_NAMES[userInfo?.roleIds[0]]}
							</span>
						</div>
						<ul className="dropdown-menu">
							<li
								onClick={() => {
									dispatch(log_out());
								}}
							>
								<a className="dropdown-item" href="#">
									Log Out
								</a>
							</li>
						</ul>
					</div>
				</div>
			</header>
		</Fragment>
	);
}
