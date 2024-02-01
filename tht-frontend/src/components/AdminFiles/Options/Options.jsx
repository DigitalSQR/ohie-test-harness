import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import "./Options.scss";
import { useLoader } from "../../loader/LoaderContext";

export default function Options(props) {
	const { refId,setSelectedOption,testcaseOptionId } = props;
	const [options, setOptions] = useState([]);
	const [currentOption, setCurrentOption] = useState();
	const { showLoader, hideLoader } = useLoader();

	useEffect(() => {
		showLoader();
		TestResultAPI.getTestCaseOptions(refId)
			.then((res) => {
				console.log(res.content);
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
	return (
		<div className="custom-multiselect field-checkbox">
			{options.map((option) => (
				<div className="field-box" key={option.id}>
					<div key={option.id} className="option-item">
						<input
							type="checkbox"
							id={option.id}
							name={option.name}
							value={option.id}
							checked={currentOption === option.id}
							onChange={handleChange}
						/>
						<label htmlFor={option.id}>{option.name}</label>
					</div>
				</div>
			))}
		</div>
	);
}

{
	/* <div className="custom-multiselect field-checkbox">
 								<div className="field-box">
 									<input
 										type="checkbox"
 										name="checkbox-choice"
 										id="checkbox-choice-1"
 										value="choice-1"
 									/>
 									<label htmlFor="checkbox-choice-1">
 										Able to see ‘link’ records action
 									</label>
 								</div> */
}
