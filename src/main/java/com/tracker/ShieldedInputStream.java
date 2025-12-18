package com.tracker;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Exposes an input stream that will not be automatically closed.
 */
public class ShieldedInputStream extends InputStream {
  private final InputStream STREAM;

  public ShieldedInputStream() {
    STREAM = System.in;
  }

  public ShieldedInputStream(InputStream stream) {
    STREAM = stream;
  }

  /**
   * Closes the input stream manually.
   * @throws IOException if an I/O error occurs.
   */
  public void closeStream() throws IOException {
    STREAM.close();
  }

  @Override
  public int available() throws IOException {
    return STREAM.available();
  }

  /**
   * This method is overridden to prevent automatic stream closing.
   * Use the method 'closeStream()' instead.
   */
  @Override
  public void close() throws IOException {
    //intentionally empty
  }

  @Override
  public void mark(int readlimit) {
    STREAM.mark(readlimit);
  }

  @Override
  public boolean markSupported() {
    return STREAM.markSupported();
  }

  @Override
  public int read() throws IOException {
    return STREAM.read();
  }

  @Override
  public int read(@NotNull byte[] b) throws IOException {
    return STREAM.read(b);
  }

  @Override
  public int read(@NotNull byte[] b, int off, int len) throws IOException {
    return STREAM.read(b, off, len);
  }

  @Override
  public @NotNull byte[] readAllBytes() throws IOException {
    return STREAM.readAllBytes();
  }

  @Override
  public @NotNull byte[] readNBytes(int len) throws IOException {
    return STREAM.readNBytes(len);
  }

  @Override
  public int readNBytes(@NotNull byte[] b, int off, int len) throws IOException {
    return STREAM.readNBytes(b, off, len);
  }

  @Override
  public void reset() throws IOException {
    STREAM.reset();
  }

  @Override
  public long skip(long n) throws IOException {
    return STREAM.skip(n);
  }

  @Override
  public void skipNBytes(long n) throws IOException {
    STREAM.skipNBytes(n);
  }

  @Override
  public long transferTo(OutputStream out) throws IOException {
    return STREAM.transferTo(out);
  }
}
