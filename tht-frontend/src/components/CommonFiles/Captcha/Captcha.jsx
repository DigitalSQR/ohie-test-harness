import React, { useState, useEffect } from "react";
import { CaptchaAPI } from "../../../api/CaptchaAPI";
import reloadCaptchaIcon from "../../../styles/images/captcha.png";
import "./_captcha.scss";
import { notification } from "antd";

const Captcha = (props) => {
    const [captcha, setCaptcha] = useState("");
    const [code, setCode] = useState("");
    const [base64Image, setbase64Image] = useState("");
    const [showMessage, setShowMessage] = useState("");
    const [minutes, setMinutes] = useState(2);
    const [seconds, setSeconds] = useState(0);

    useEffect(() => {
        const isCaptchaRequired = process.env.REACT_APP_CAPTCHA;
        if (isCaptchaRequired === 'true') {
            const interval = setInterval(() => {
                if (seconds === 0) {
                    if (minutes === 0) {
                        clearInterval(interval);
                        reloadCaptcha();
                    } else {
                        setMinutes(minutes - 1);
                        setSeconds(59);
                    }
                } else {
                    setSeconds(seconds - 1);
                }
            }, 1000);


            setShowMessage(<p className="captcha-message">Captcha refreshes in {minutes}:{seconds < 10 ? `0${seconds}` : seconds}</p>)

            return () => clearInterval(interval);
        }
    }, [minutes, seconds]);

    useEffect(() => {
        const isCaptchaRequired = process.env.REACT_APP_CAPTCHA;
        if (isCaptchaRequired === 'true') {
            fetchCaptcha();
        }
    }, [])

    useEffect(() => {
        const isCaptchaRequired = process.env.REACT_APP_CAPTCHA;
        if (isCaptchaRequired === 'true') {
            props.getCaptcha(code, captcha);
        }
    }, [captcha, code])

    const reloadCaptcha = () => {
        setCode("");
        setCaptcha("");
        setShowMessage(<></>)
        fetchCaptcha();
        setMinutes(2);
        setSeconds(0);
    }

    const fetchCaptcha = async () => {
        try {
            const response = await CaptchaAPI.getCaptcha();
            setCaptcha(response.data.captcha);
            setbase64Image(response.data.image);
        } catch (error) {
            notification.error({
                className:"notificationError",
                message: "Something went wrong while fetching captcha",
                placement: "bottomRight",
            });
        }
    };

    return (
        <div id="captcha">
            {
                base64Image ?
                    <div className="custom-input mb-3">
                        <label htmlFor="exampleFormControlInput1" className="form-label">
                            Captcha
                        </label>
                        <div className="mb-3">
                            <img src={`data:image/png;base64, ${base64Image}`} alt="Captcha Image" />
                            <button className="reload-icon" onClick={reloadCaptcha} id="#Captcha-1">
                                <img src={reloadCaptchaIcon} alt="reload captcha" />
                            </button>
                        </div>
                        {showMessage}
                        <div className="input-group">
                            <span className="input-group-text" id="basic-addon1">
                                <i className="bi bi-person-lock"></i>
                            </span>
                            <input
                                name="captcha"
                                type="text"
                                value={code}
                                className="form-control border-start-0 ps-0"
                                placeholder="Please type the above captcha"
                                aria-label="captcha"
                                aria-describedby="basic-addon1"
                                onChange={(event) => setCode(event.target.value)}
                                autoComplete="off"
                            />
                        </div>
                    </div>
                    :
                    <></>
            }
        </div>
    )
}

export default Captcha

