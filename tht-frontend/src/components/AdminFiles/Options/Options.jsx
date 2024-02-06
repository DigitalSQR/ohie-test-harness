import { useEffect, useRef, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import "./Options.scss";
import { useLoader } from "../../loader/LoaderContext";

export default function Options(props) {
	const { refId, setSelectedOption, testcaseOptionId } = props;
	const [options, setOptions] = useState([]);
	const [currentOption, setCurrentOption] = useState();
	const { showLoader, hideLoader } = useLoader();
	const inputRef = useRef(new Array());

	useEffect(() => {
		showLoader();
		TestResultAPI.getTestCaseOptions(refId)
			.then((res) => {
				// console.log(res.content);
				setOptions(res.content);
				hideLoader();
			})
			.catch((error) => {
				throw error;
			});
		setCurrentOption(testcaseOptionId);
		setSelectedOption(testcaseOptionId);

	}, []);

	const handleChange = (e) => {
		setCurrentOption(e.target.value);
		setSelectedOption(e.target.value);
	}

	const onLabelClick = (e, index) => {
		if (inputRef.current[index]) {
			inputRef.current[index].click();
		}
	}

	return (
		<div className="custom-multiselect field-checkbox">
			{options.map((option, index) => (
				<div className="field-box" key={option.id}>
					<div key={option.id} className="option-item">
						<input
							ref={(element) => inputRef.current.push(element)}
							type="checkbox"
							id={option.id}
							name={option.name}
							value={option.id}
							checked={currentOption === option.id}
							onChange={handleChange}
							autoComplete="off"
						/>
						<label htmlFor={option.id} onClick={(e) => onLabelClick(e, index)}>{option.name}</label>
					</div>
				</div>
			))}
		</div>
	);
}