# Medizintechnik-II-Final-Project

# Image Processing Techniques Project

## Overview

This repository contains the code and documentation for a project focused on implementing and evaluating various image processing techniques. The project is part of the Medizintechnik II course and is designed to enhance understanding of fundamental image processing methods, particularly in the context of medical imaging.

The project includes the following key components:
- **Thresholding Techniques**: Implementation of basic and advanced thresholding methods, including Otsu's method.
- **Edge Detection**: Application of edge detection filters such as Sobel, Scharr, Prewitt, and Canny.
- **Segmentation Evaluation**: Analysis of segmentation results using metrics like sensitivity, specificity, Dice Coefficient, and Jaccard Index.
- **User Interface**: A custom interface that allows users to select and apply different image processing techniques to grayscale images.

## Project Structure

The repository is structured as follows:

├── src/
│   ├── Task_1_Threshold.java
│   ├── Task_2_EvaluateSegmentation.java
│   ├── Task_3_Otsu.java
│   ├── Task_4_Filters.java
│   ├── Task_5_CannyEdgeDetection.java
│   ├── User_Interface.java
│   └── EvaluationResult.java
├── latex-template/
│   ├── report.tex
│   ├── bibliography.bib
│   └── figures/
│       ├── figure1.png
│       ├── figure2.png
│       └── ...
├── README.md


### `src/`
This directory contains all Java source files that implement the image processing techniques, including thresholding, edge detection, and evaluation of segmentation. The `User_Interface.java` file provides a user-friendly interface for interacting with these techniques.

### `latex-template/`
This directory contains the LaTeX source files for the project report. It includes:
- `report.tex`: The main LaTeX document for the project report.
- `bibliography.bib`: The BibTeX file containing references used in the report.
- `figures/`: A folder containing the figures used in the report.

## How to Run the Project

1. **Clone the Repository**:

`git clone https://github.com/yourusername/your-repo-name.git cd your-repo-name`


2. **Set Up the Java Environment**:
- Ensure you have Java Development Kit (JDK) installed.
- Use an IDE like IntelliJ IDEA or Eclipse to open the project.

3. **Compile and Run**:
- Compile the Java classes.
- Run the `User_Interface.java` class to start the interface.
- Select the desired image processing technique and apply it to the image.

4. **Generate the Report**:
- Navigate to the `latex-template/` directory.
- Compile the `report.tex` file using a LaTeX editor like Overleaf or a local LaTeX distribution (e.g., TeX Live, MikTeX).

## Contributing

Contributions are welcome! Please fork this repository and submit a pull request with your improvements.

## Contact

For any questions or issues, please contact [mr.hassanch@gmail.com](mailto:mr.hassanch@gmail.com).
