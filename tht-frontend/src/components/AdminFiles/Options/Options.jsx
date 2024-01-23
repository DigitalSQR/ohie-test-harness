import { useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import "./Options.css"
export default function Options(props) {
	const { refId } = props;
	const [options, setOptions] = useState([]);
	useEffect(() => {
		TestResultAPI.getTestCaseOptions(refId)
			.then((res) => {
				console.log(res.content);
				setOptions(res.content);
			})
			.catch((error) => {
				throw error;
			});
	}, []);
	
	return (
		<div className="options-container">
			{options.map((option) => (
				<div key={option.id} className="option-item">
					<input type="radio" id={option.id} name={option.name} />
					<label htmlFor={option.name}>{option.name}</label>
				</div>
			))}
		</div>
	);
}
