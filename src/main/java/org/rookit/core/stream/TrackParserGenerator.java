/*******************************************************************************
 * Copyright (C) 2017 Joao Sousa
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.rookit.core.stream;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.rookit.core.config.ParsingConfig;
import org.rookit.core.utils.CoreValidator;
import org.rookit.mongodb.DBManager;
import org.rookit.parser.config.ParserConfiguration;
import org.rookit.parser.formatlist.FormatList;
import org.rookit.parser.parser.Parser;
import org.rookit.parser.parser.ParserFactory;
import org.rookit.parser.parser.ParserPipeline;
import org.rookit.parser.result.Result;
import org.rookit.parser.result.SingleTrackAlbumBuilder;
import org.rookit.parser.utils.DirectoryFilters;
import org.rookit.parser.utils.PathUtils;
import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.builder.StreamGenerator;

@SuppressWarnings("javadoc")
public class TrackParserGenerator implements StreamGenerator<Path, TPGResult>, Parser<TrackPath, SingleTrackAlbumBuilder>, AutoCloseable {

	private final CoreValidator validator;

	private Stream<Path> stream;
	private final Parser<TrackPath, SingleTrackAlbumBuilder> parser;
	private final ParserFactory factory;
	private final ParsingConfig config;
	private final ParserConfiguration parserConfig;

	public TrackParserGenerator(DBManager db, ParsingConfig config) {
		super();
		validator = CoreValidator.getDefault();
		validator.checkArgumentNotNull(db, "The database cannot be null");
		validator.checkArgumentNotNull(config, "The configuration cannot be null");
		factory = ParserFactory.create();
		this.config = config;
		this.parserConfig = buildParserConfig(db, config);
		this.parser = buildParser(this.parserConfig);
	}

	private ParserPipeline<TrackPath, String, SingleTrackAlbumBuilder> buildParser(ParserConfiguration config) {
		return factory
				.newParserPipeline(TrackPath.class, SingleTrackAlbumBuilder.create())
				.insert(factory.newTagParser(config))
				.mapInput(PathUtils::getFileName)
				.insert(factory.newFormatParser(config));
	}

	private ParserConfiguration buildParserConfig(DBManager db, ParsingConfig config) {
		return Parser.createConfiguration(SingleTrackAlbumBuilder.class)
				.withDBConnection(db)
				.withDbStorage(true)
				.withTrackFormats(readFormats(config).getAll().collect(Collectors.toList()))
				.withLimit(config.getParserLimit());
	}

	private FormatList readFormats(ParsingConfig config) {
		try {
			return FormatList.readFromPath(config.getFormatsPath());
		} catch (IOException e) {
			validator.handleIOException(e);
			return null;
		}
	}

	@Override
	public Stream<TPGResult> generate(Path source) {
		stream = list(source);
		return stream
				//filters tracks
				.filter(DirectoryFilters.newTrackStreamFilter())
				// TODO filter track by format
				.map(p -> TrackPath.create(p))
				.map(p -> new TPGResult(p, parseAll(p)));
	}

	private Stream<Path> list(Path source) {
		try {
			return Files.list(source);
		} catch (IOException e) {
			validator.handleIOException(e);
			return null;
		}
	}

	@Override
	public void close() {
		stream.close();
	}

	@Override
	public Optional<SingleTrackAlbumBuilder> parse(TrackPath arg0) {
		return parser.parse(arg0);
	}

	@Override
	public <O extends Result<?>> Optional<SingleTrackAlbumBuilder> parse(TrackPath arg0, O arg1) {
		return parser.parse(arg0, arg1);
	}

	@Override
	public Iterable<SingleTrackAlbumBuilder> parseAll(TrackPath arg0) {
		return filter(parser.parseAll(arg0));
	}

	@Override
	public <O extends Result<?>> Iterable<SingleTrackAlbumBuilder> parseAll(TrackPath arg0, O arg1) {
		return filter(parser.parseAll(arg0, arg1));
	}

	private Iterable<SingleTrackAlbumBuilder> filter(Iterable<SingleTrackAlbumBuilder> results) {
		return StreamSupport.stream(results.spliterator(), false)
				.filter(result -> !config.isFilterNegatives() || result.getScore() > 0)
				.limit(config.getParserLimit())
				.collect(Collectors.toList());
	}

	@Override
	public ParserConfiguration getConfig() {
		return parserConfig;
	}

}
