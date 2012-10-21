package org.deploymentobjects.core.domain.model.execution;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;

public class EntryPoint {

	/**
	 * EntryPoint 
	 * Intended to be run from run.sh 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("file").withDescription(
				"the file containing commands").hasArg().withArgName("file")
				.isRequired().create("f"));
		CommandLineParser parser = new PosixParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
		} catch (MissingOptionException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("run.sh", options, true);
			return;
		}
		String fileName = cmd.getOptionValue("file");
		
			String contents = FileUtils.readFileToString(new File(fileName));
			Program program = Program.factory(contents);
		
	}

}
