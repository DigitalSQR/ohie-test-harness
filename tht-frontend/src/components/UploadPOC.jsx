import { useCallback, useRef, useState } from "react";
import { useDropzone } from "react-dropzone";
import html2canvas from "html2canvas";
import jsPDF from "jspdf";

export default function UploadTestCases() {
  const pdfRef = useRef();
  const [files, setFiles] = useState([]);
  const onDrop = useCallback((acceptedFiles) => {
    console.log("the accepted files are ", acceptedFiles);
    if (acceptedFiles?.length) {
      setFiles((prevFiles) => [
        ...prevFiles,
        ...acceptedFiles.map((file) => {
          return Object.assign(file, { preview: URL.createObjectURL(file) });
        }),
      ]);
    }
  }, []);

  const DeleteHandler = (deletedname) => {
    setFiles((files) => files.filter((file) => file.name !== deletedname));
  };
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      "image/*": [],
    },
  });

  const downloadPdfHandler = () => {
    const input = pdfRef.current;
    console.log(input);
    html2canvas(input).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF("p", "mm", "a4", true);
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = pdf.internal.pageSize.getHeight();
      const imgWidth = canvas.width;
      const imgHeight = canvas.height;
      const ratio = Math.min(pdfWidth / imgWidth, pdfHeight / imgHeight);
      const imgX = (pdfWidth - imgWidth * ratio) / 2;
      const imgY = 30;
      pdf.addImage(
        imgData,
        "PNG",
        imgX,
        imgY,
        imgWidth * ratio,
        imgHeight * ratio
      );
      pdf.save("report.pdf");
    });
  };

  return (
    <div>
      <div
        style={{
          border: "2px dashed #888",
          padding: "20px",
          textAlign: "center",
        }}
        {...getRootProps()}
      >
        <input {...getInputProps()} />
        {isDragActive ? (
          <p>Drop the files here ...</p>
        ) : (
          <p>Drag 'n' drop some files here, or click to select files</p>
        )}
      </div>
      <div ref={pdfRef}>
        <h2>Accepted Files</h2>
        {/* <h3>{<img src={dys}/>}</h3> */}
        {files.map((file) => {
          return (
            <div key={file.name}>
              <img
                src={file.preview}
                style={{ height: 100 }}
                alt="No Preview Available"
              />
              <br />
              <button
                type="alert"
                style={{ height: 40, textSizeAdjust: "flex" }}
                onClick={() => DeleteHandler(file.name)}
              >
                <p>Delete</p>
              </button>
            </div>
          );
        })}
      </div>

      <button onClick={downloadPdfHandler} disabled={files.length === 0} >Download PDF</button>
    </div>
  );
}
