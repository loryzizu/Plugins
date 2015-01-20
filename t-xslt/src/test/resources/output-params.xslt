<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="text" omit-xml-declaration="yes" indent="no" />

	<!-- parameter declaration -->
	<xsl:param name="one" />
	<xsl:param name="two" />

	<xsl:template match="/">
		<xsl:value-of select="$one" />
		<xsl:value-of select="$two" />
	</xsl:template>
</xsl:stylesheet>