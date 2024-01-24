import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import "./Options.scss";
import { useLoader } from "../../loader/LoaderContext";

export default function Options(props) {
	const { refId } = props;
	const [options, setOptions] = useState([]);
	const [selectedOption, setSelectedOption] = useState(null);
	const {showLoader,hideLoader} = useLoader();

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
	}, []);

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
							checked={selectedOption === option.id}
							onChange={(e) => setSelectedOption(e.target.value)}
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
