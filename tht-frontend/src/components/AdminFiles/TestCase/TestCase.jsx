import { Fragment, useEffect, useState } from "react";
import { TestResultAPI } from "../../../api/TestResultAPI";
import Options from "../Options/Options";
import { Pagination } from "@mui/material";
import { useLoader } from "../../loader/LoaderContext";
import { notification } from "antd";
import "./testcase.scss";
import { DocumentAPI } from "../../../api/DocumentAPI";
import { fileTypeIcon } from "../../../utils/utils";
export default function TestCase(props) {
	const { specificationId, nextSpecification } = props;
	const [manualQuestions, setManualQuestions] = useState([]);
	const [currentPage, setCurrentPage] = useState(1);
	const [totalPage, setTotalPage] = useState(1);
	const { showLoader, hideLoader } = useLoader();
	const [selectedOption, setSelectedOption] = useState();
	const [files, setFiles] = useState([]);
	const [uploadedFiles, setUploadedFiles] = useState([]);
	const handlePageChange = (event, page) => {
		showLoader();
		setCurrentPage(page);
		fetchQuestions(page);
		hideLoader();
		setSelectedOption(null);
	};

	const handleSaveandNext = (id, currentPage, rank, index) => {
		console.log(
			currentPage,
			rank,
			index,
			totalPage,
			manualQuestions.length
		);
		if (selectedOption == null) {
			notification.error({
				description: "No answers selected",
				placement: "bottomRight",
			});
		} else {
			TestResultAPI.saveOptions(id, selectedOption)
				.then((res) => {
					console.log(res);
					if (currentPage == totalPage) {
						console.log("arrives in the last page");
						nextSpecification(rank + 1);
					} else if (currentPage < totalPage) {
						setCurrentPage((currentPage) => {
							return currentPage + 1;
						});
						fetchQuestions(currentPage + 1);
					}
				})
				.catch((error) => {
					throw error;
				});
		}
	};

	const fetchQuestions = (currentPage) => {
		TestResultAPI.getQuestions(specificationId, currentPage - 1)
			.then((res) => {
				console.log(res);
				setTotalPage(res.totalPages);
				setManualQuestions(res.content);
			})
			.catch((error) => {
				throw error;
			});
	};
	useEffect(() => {
		fetchQuestions(currentPage);
	}, []);

	const addAttachment = () => {
		var file = document.getElementById("my-file");
		if (file) file.click();
	};

	const handleChange = (event) => {
		setFiles([...event.target.files]);
	};

	const onUpload = (event) => {
		event.preventDefault();
		files.forEach((file) => {
			const formData = new FormData();
			formData.append(`file`, file);
			formData.append(`fileName`, file.name);
			formData.append("refId", "abcabc");
			formData.append("refObjUri", "abcabc");
			DocumentAPI.uploadDocument(formData)
				.then((res) => {
					console.log(res);
					setUploadedFiles((prevFiles) => [
						...prevFiles,
						{ name: res.name, id: res.id, fileType: res.fileType },
					]);
				})
				.catch((err) => {
					console.log(err);
				});
		});
	};

	const downloadFile = (file) => {
		DocumentAPI.downloadDocument(file.id, file.name).catch((err) => {
			console.error(err.data.message);
		});
	};
	return (
		<Fragment>
			<div className="col-12 non-fuctional-requirement">
				<div className="container">
					<div className="row heading">
						<div className="col-md-9 col-12 p-0">
							<h2>Question</h2>
						</div>
						<div className="col-md-3 col-12 d-md-flex d-none p-0">
							<h2 className="border-left">Reference</h2>
						</div>
					</div>
					{manualQuestions.map((question, index) => {
						console.log(question.testcaseOptionId);
						const segments = question.refId.split(".");
						const Specification = segments
							.slice(-3)
							.join(".")
							.toUpperCase();
						return (
							<div className="row question-box" key={question.id}>
								{/* <div className="col-md-9 col-12 p-0 question">  for the image space*/}

								<div className=" col-12 p-0 question">
									<h2>
										<b>
											{Specification +
												" " +
												question.name +
												" "}
										</b>
									</h2>
									<Options
										refId={question.refId}
										setSelectedOption={setSelectedOption}
										testcaseOptionId={
											question.testcaseOptionId
										}
									></Options>
								</div>
								<div className="col-md-9 col-12 p-0"></div>
								{/* Photos upload code below */}
								<div className="mb-3">
									<div
										className="cst-btn-group btn-group"
										role="group"
										aria-label="Basic example"
										style={{ margin: "0 15px" }}
									>
										<input
											type="file"
											multiple
											name="my_file"
											id="my-file"
											onChange={handleChange}
											style={{
												visibility: "hidden",
												width: "0",
											}}
										></input>
										<button
											type="button"
											className="btn cst-btn-default"
											onClick={addAttachment}
										>
											<i
												style={{
													transform:
														"rotate(-45.975deg)",
												}}
												className="bi bi-paperclip"
											></i>
											Add Attachments
										</button>
										<button
											type="button"
											className="btn cst-btn-default"
										>
											<i className="bi bi-chat-right-text"></i>
											Add Notes
										</button>
									</div>
								</div>
								<div className="doc-badge-wrapper">
									{uploadedFiles.map((file) => (
										<div
											type="button"
											key={file.id}
											className="doc-badge"
											onClick={() => downloadFile(file)}
										>
											<img
												src={fileTypeIcon(
													file.fileType
												)}
											/>
											<p> {file.name} </p>
										</div>
									))}
									<button
										type="button"
										className="btn btn-sm btn-primary"
										onClick={onUpload}
									>
										Upload
									</button>
								
								</div>
								{/* Photos upload code above */}

								<span>
									<button
										className="btn btn-primary"
										onClick={() => {
											handleSaveandNext(
												question.id,
												currentPage,
												question.rank,
												index
											);
										}}
									>
										Save and Next
									</button>
								</span>
							</div>
						);
					})}
				</div>
			</div>
			<div style={{ display: "flex", justifyContent: "space-between" }}>
				<Pagination
					style={{
						flex: 1,
						display: "flex",
						justifyContent: "center",
					}}
					count={totalPage}
					page={currentPage}
					color="primary"
					onChange={handlePageChange}
				/>
				{/* <button></button> */}
			</div>
		</Fragment>
	);
}
