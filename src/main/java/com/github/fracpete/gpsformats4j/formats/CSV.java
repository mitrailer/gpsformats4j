/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * CSV.java
 * Copyright (C) 2016 FracPete
 */

package com.github.fracpete.gpsformats4j.formats;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVRecordFactory;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV format. Requires the following columns in this order:
 * <ol>
 *   <li>track</li>
 *   <li>time</li>
 *   <li>latitude</li>
 *   <li>longitude</li>
 *   <li>elevation</li>
 * </ol>
 *
 * @author FracPete (fracpete at gmail dot com)
 */
public class CSV
  extends AbstractFormat {

  /**
   * Returns whether reading is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean canRead() {
    return true;
  }

  /**
   * Reads the file.
   *
   * @param input	the input file
   * @return		the collected data, null in case of an error
   */
  @Override
  public List<CSVRecord> read(File input) {
    List<CSVRecord>	result;
    CSVParser		parser;

    result = new ArrayList<>();
    try {
      m_Logger.info("Reading: " + input);
      parser = CSVParser.parse(input, Charset.defaultCharset(), CSVFormat.DEFAULT);
      for (CSVRecord rec: parser)
	result.add(rec);
    }
    catch (Exception e) {
      m_Logger.error("Failed to read: " + input, e);
      return null;
    }

    return result;
  }

  /**
   * Returns whether writing is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean canWrite() {
    return true;
  }

  /**
   * Writes to a file.
   *
   * @param data	the data to write
   * @param output	the output file
   * @return		null if successful, otherwise error message
   */
  @Override
  public String write(List<CSVRecord> data, File output) {
    CSVPrinter		printer;
    FileWriter		writer;
    boolean		first;
    int			i;
    CSVRecord		header;
    List<String>	values;
    Map<String,Integer>	map;

    writer  = null;
    printer = null;
    try {
      m_Logger.info("Writing: " + output);
      writer  = new FileWriter(output);
      printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
      first   = true;
      for (CSVRecord rec: data) {
	if (first) {
	  map = new HashMap<>();
	  map.put(KEY_TRACK, 0);
	  map.put(KEY_TIME, 1);
	  map.put(KEY_LAT, 2);
	  map.put(KEY_LON, 3);
	  map.put(KEY_ELEVATION, 4);
	  values = new ArrayList<>();
	  values.add(KEY_TRACK);
	  values.add(KEY_TIME);
	  values.add(KEY_LAT);
	  values.add(KEY_LON);
	  values.add(KEY_ELEVATION);
	  header = CSVRecordFactory.newRecord(values.toArray(new String[values.size()]), map);
	  printer.printRecord(header);
	}
	printer.printRecord(rec);
	first = false;
      }
      printer.flush();
      printer.close();
    }
    catch (Exception e) {
      m_Logger.error("Failed to write: " + output, e);
      return "Failed to write: " + output + "\n" + e;
    }
    finally {
      IOUtils.closeQuietly(writer);
      IOUtils.closeQuietly(printer);
    }

    return null;
  }
}
