# viral-sequence-classifier
This application classifies a sequence from an unknown virus into its likely viral family using three variants of the Smith-Waterman algorithm. It accepts the unknown virus sequence in two formats: FASTA file and string.

# How to run the application
The application has a command-line interface. You can run the application with a series of arguments to customize the classification and what kind of results you would like to see. You can run the application from their terminal or from an integrated development environment (IDE). To run the application from an IDE, navigate to the ViralClassifierApp class in the main/ folder and run the application. You can add command line arguments by editing the configuration.

To run the application from the terminal, you can navigate to the project’s root directory and navigate to the classes by entering “cd target/classes”. 
Next, you can enter java com.asiahaslam.viralclassifier.main.ViralClassifierApp -h. This argument, -h, or --help, will take you to the built-in help menu where you can see a list of all the possible command line arguments for the app.

# Choosing the input type
To run viral classification, you can choose whether you would like to input the unknown sequence by providing the path to a FASTA file or by pasting in a sequence directly. To request that the application classify a sequence from a FASTA file, enter -f or --file followed by the file path. For example, -f data/unknown.fasta. If you would like to enter the sequence directly, enter -s or --sequence followed by the sequence itself. For example (with an extremely short sequence example), -s AATGCTC. 

# Choosing the algorithm variant(s)
You can also pick which Smith-Waterman variant(s) you would like to use for classification. Enter -v or --variant followed by one of the following: standard, space, banded, or all. For example, -v all, which would run all three variants at once and allow you to compare the performance of the different variants.

# Providing custom parameters
You can provide a custom band width (if using the Banded Smith-Waterman variant or all variants) or confidence threshold if you would like. To provide a custom band width, enter -b or --band-width followed by an integer corresponding to the desired band width. The default band width is 20. To provide a custom confidence threshold, enter -c or --confidence followed by the desired confidence interval, which must be a double from 0.5 to 1.0, inclusive. The default confidence threshold is 0.70.

# Customizing the classification results
The application provides quite a few options for customizable classification results. By default, it shows which algorithm it used, the predicted viral family, the confidence score, and whether or not the prediction is confident (whether the confidence score is above the confidence threshold).

In addition to the default results, you can choose to see information for the second-best viral family prediction (with the argument -t or --two-families), more information about the maximum alignment score for the top family prediction (with the argument -m or --max-score), data about algorithm performance (with the argument -p or --performance), or view the optimal alignment between the unknown sequence and the sequence with the top alignment score (with the argument -a or --alignment).

# All possible command line arguments
In summary, these are the possible arguments that you can provide to the application:

Help menu
* -h or --help 

Providing unknown sequence
* -s <seq> or --sequence <seq> where seq is a viral sequence
* -f <filePath> or --file <filePath> where filePath is the path to the file containing the unknown sequence

Algorithm choice
* -v <type> or --v <type> where type is one of the following: “standard”, “space”, “banded” or “all”

Configuration options
* -b <k> or --band-width <k> where k is an integer > 0
* -c <conf> or --confidence <conf> where conf is a double from 0.5 to 1.0, inclusive
* -t or --two-families
* -m or --max-score
* -a or --alignment
* -p or --performance

# Running unit tests
To run any of the tests, navigate to the src/test/ folder in your IDE. Simply select the file with the test you would like to run, and click "Run."
